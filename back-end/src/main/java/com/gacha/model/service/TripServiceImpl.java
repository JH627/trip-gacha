package com.gacha.model.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gacha.exception.TripErrorCode;
import com.gacha.exception.TripException;
import com.gacha.model.dao.SpotDao;
import com.gacha.model.dao.DestinationDao;
import com.gacha.model.dao.TripScheduleDao;
import com.gacha.model.dto.enums.ImageCategory;
import com.gacha.model.dto.enums.SpotCategory;
import com.gacha.model.dto.enums.SpotSearchCondition;
import com.gacha.model.dto.trip.BookmarkSpotRequest;
import com.gacha.model.dto.trip.DestinationInfo;
import com.gacha.model.dto.trip.ScheduleDetail;
import com.gacha.model.dto.trip.ScheduleInfo;
import com.gacha.model.dto.trip.ScheduleRegistFormRequest;
import com.gacha.model.dto.trip.SpotInfo;
import com.gacha.model.dto.trip.SpotRegistFormRequest;
import com.gacha.util.ImageUtil;
import com.gacha.model.dto.trip.SpotListResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {
	
	private final SpotDao spotDao;
	private final DestinationDao destinationDao;
	private final TripScheduleDao tripScheduleDao;
	private final ImageUtil imageUtil;

	@Override
	@Transactional
	public void toggleSpotBookmark(Integer userId, BookmarkSpotRequest dto) {
		boolean isBookmarked = spotDao.isBookmarked(userId, dto.getSpotId());
		
		try {
			if (isBookmarked) {
				spotDao.deleteBookmark(userId, dto.getSpotId());
			}
			else {
				if (!spotDao.existsSpot(dto.getSpotId())) {
					throw new TripException(TripErrorCode.SPOT_NOT_FOUND);
				}
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
	public SpotListResponse getSpotList(Integer userId, Integer destinationId, String keyword, 
			SpotCategory category, SpotSearchCondition sort, Integer page) {		
		List<SpotInfo> spots;
		int pageSize = 12;
		int offset = page * pageSize;
		
		// 찜 목록
		if (category == SpotCategory.MARKED) {
			spots = spotDao.selectBookmarkedSpots(userId, destinationId, keyword, sort.name(), offset, pageSize);
		}
		// 전체 관광지
		else if (category == SpotCategory.ALLSPOT) {
			spots = spotDao.selectAllSpots(userId, destinationId, keyword, sort.name(), offset, pageSize);
		}
		// 카테고리별 목록
		else {
			spots = spotDao.selectByDesinationIdAndCategory(userId, destinationId, category.name(), keyword, sort.name(), offset, pageSize);
		}
		
		int total = spotDao.getTotalCount();
		
		return SpotListResponse.builder()
				.spots(spots)
				.total(total)
				.build();
	}

	@Override
	@Transactional
	public void registSpot(Integer userId, SpotRegistFormRequest form) {
		String imgUrl = "";
		boolean uploaded = false;

	    if (form.getImg() != null && !form.getImg().isEmpty()) {
	        try {
				imgUrl = imageUtil.upload(form.getImg(), ImageCategory.spot);
				uploaded = true;
	        } catch (Exception e) {
	        	log.info("이미지 파일 S3 업로드 실패");
	            throw e;
	        }
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
			uploaded = false;
		} catch (DataIntegrityViolationException e) {
			throw new TripException(TripErrorCode.DESTINATION_NOT_FOUND);
		} catch (Exception e) {
			throw e;
		} finally {
			// DB에 관광지 정보 등록 실패 시 S3이미지 롤백
			if (uploaded) {
//				s3Service.deleteImage(imgUrl);  // 실패 시 이미지 삭제
			}
		}
	}

	@Override
	@Transactional
	public void registSchedule(Integer userId, ScheduleRegistFormRequest form) {
		try {
			// 일정 등록
			tripScheduleDao.insertSchedule(userId, form);
			Integer scheduleId = form.getTripScheduleId();
			
			// 일정 아이템 등록
			tripScheduleDao.insertScheduleItems(form.getScheduleItems(), scheduleId);
		} catch (DataIntegrityViolationException e) {
			// 목적지 ID 예외
			if (e.getMessage().contains("destination_id")) {
				throw new TripException(TripErrorCode.DESTINATION_NOT_FOUND);
			} 
			// 관광지 ID 예외
			else if (e.getMessage().contains("spot_id")) {
				throw new TripException(TripErrorCode.SPOT_NOT_FOUND);
			}
			throw e;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ScheduleInfo> getScheduleList(Integer userId) {
		return tripScheduleDao.selectAllScheduleByUserId(userId);
	}

	@Override
	@Transactional(readOnly = true)
	public ScheduleDetail getScheduleDetail(Integer userId, Integer scheduleId) {
		// 만약 내가 조회하거나 공유가 설정되어있다면 나를 제외한 사용자도 정보를 가져올 수 있음
		// 그렇지 않은 경우는 접근 불가
		if (!tripScheduleDao.checkIsShared(userId, scheduleId)) {
			throw new TripException(TripErrorCode.SCHEDULE_FORBIDDEN);
		}
		
		return tripScheduleDao.selectScheduleByUserId(userId, scheduleId);
	}
	
	@Override
	@Transactional
	public ScheduleDetail toggleScheduleShare(Integer userId, Integer scheduleId) {
		// 자신의 일정인지 확인
		if (!tripScheduleDao.isOwner(userId, scheduleId)) {
			throw new TripException(TripErrorCode.SCHEDULE_FORBIDDEN);
		}
		
		// 공유 상태 토글
		tripScheduleDao.toggleShareStatus(scheduleId);
		
		// 변경된 일정 정보 반환
		return tripScheduleDao.selectScheduleByUserId(userId, scheduleId);
	}
}
