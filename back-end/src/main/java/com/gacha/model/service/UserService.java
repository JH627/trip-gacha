package com.gacha.model.service;

import com.gacha.model.dto.user.FullUserInfo;
import com.gacha.model.dto.user.RegistRequest;
import com.gacha.model.dto.user.UpdateRequest;
import com.gacha.model.dto.user.UserStat;

public interface UserService {
	/**
	 * 회원가입
	 * 
	 * @param registRequest 회원가입정보
	 */
	void regist(RegistRequest registRequest);

	/**
	 * 유저의 필수 정보 반환
	 * 
	 * @param userId 사용자ID
	 * @return 유저 정보(이메일, 닉네임, 프로필 이미지 링크)
	 */
	FullUserInfo searchUserInfo(Integer userId);
	
	/**
	 * 유저의 부가 정보 반환
	 * 
	 * @param userId 사용자ID 
	 * @return 유저 정보(가입 이후 함께한 일수, 생성한 일정 개수, 찜한 관광지 개수, 작성한 게시글 수)
	 * 
	 */
	UserStat getUserStats(Integer userId);

	/**
	 * 유저 정보 업데이트
	 * 
	 * @param userId 사용자ID 
	 * @param updateRequest 변경할 정보(닉네임, 사진)
	 */
	void updateProfile(Integer userId, UpdateRequest updateRequest);
}
