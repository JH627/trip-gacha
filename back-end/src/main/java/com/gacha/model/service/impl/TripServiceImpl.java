package com.gacha.model.service.impl;

import com.gacha.model.dao.TripScheduleDao;
import com.gacha.model.exception.TripException;
import com.gacha.model.exception.TripErrorCode;
import com.gacha.model.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TripServiceImpl implements TripService {

	@Autowired
	private TripScheduleDao tripScheduleDao;

	@Override
	public void toggleScheduleShare(Integer userId, Integer scheduleId) {
		// 일정 소유자 확인
		if (!tripScheduleDao.isOwner(userId, scheduleId)) {
			throw new TripException(TripErrorCode.NOT_OWNER);
		}
		
		// 공유 상태 토글
		tripScheduleDao.toggleShareStatus(scheduleId);
	}
} 