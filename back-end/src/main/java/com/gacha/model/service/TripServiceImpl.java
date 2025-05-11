package com.gacha.model.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gacha.exception.TripErrorCode;
import com.gacha.exception.TripException;
import com.gacha.model.dao.SpotDao;
import com.gacha.model.dao.DestinationDao;
import com.gacha.model.dto.request.TripRequest;
import com.gacha.model.dto.request.TripRequest.SpotCategory;
import com.gacha.model.dto.request.TripRequest.SpotRegistForm;
import com.gacha.model.dto.request.TripRequest.SpotSearchCondition;
import com.gacha.model.dto.response.trip.DestinationInfo;
import com.gacha.model.dto.response.trip.SpotInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {
	
	private final SpotDao spotDao;
	private final DestinationDao destinationDao;

	@Override
	@Transactional(readOnly = true)
	public List<SpotInfo> getRecommendSpotList(Integer userId) {
		return spotDao.selectAll(userId);
	}

	@Override
	@Transactional
	public void toggleSpotBookmark(Integer userId, TripRequest.BookmarkSpot dto) {
		boolean isBookmarked = spotDao.isBookmarked(userId, dto.getSpotId());
		
		try {
			if (isBookmarked) {
				spotDao.deleteBookmark(userId, dto.getSpotId());
			}
			else {
				spotDao.addBookmark(userId, dto.getSpotId());
			}
		} catch (DataIntegrityViolationException e) {
			throw new TripException(TripErrorCode.SPOT_NOT_FOUND);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<DestinationInfo> getDestinationList(String keyword) {
		return destinationDao.selectByKeyword(keyword);
	}

	@Override
	@Transactional(readOnly = true)
	public List<SpotInfo> getSpotList(Integer userId, Integer destinationId, String keyword, 
			SpotCategory category, SpotSearchCondition sort, Integer page) {		
		// 찜 목록
		if (category == SpotCategory.MARKED) {
			return spotDao.selectBookmarkedSpots(userId, destinationId, keyword, sort.name(), page);
		}
		
		// 카테고리별 목록
		return spotDao.selectByDesinationIdAndCategory(userId, destinationId, category.name(), keyword, sort.name(), page);
	}

	@Override
	@Transactional
	public void registSpot(Integer userId, SpotRegistForm form) {
		String imgUrl = "";
		boolean uploaded = false;

		if (form.getImg() != null && !form.getImg().isEmpty()) {
			// TODO : 이미지 파일 S3에 올리기 -> 반환 url 저장
//			imgUrl = s3Service.uploadImage(form.getImg());
//			uploaded = true;
		}

		try {
			spotDao.insertSpot(
				form.getDestinationId(),
				form.getName(),
				form.getContent(),
				imgUrl,
				form.getAddress(),
				form.getCategory().name()
			);
		} catch (Exception e) {
			// DB에 관광지 정보 등록 실패 시 S3이미지 롤백
			if (uploaded) {
//				s3Service.deleteImage(imgUrl);  // 실패 시 이미지 삭제
			}
			throw e;
		}
	}

	
	
}
