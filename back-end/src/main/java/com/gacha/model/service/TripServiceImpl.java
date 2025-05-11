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
	
	
}
