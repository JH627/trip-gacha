package com.gacha.model.service;

import com.gacha.model.dto.user.FullUserInfo;
import com.gacha.model.dto.user.LoginRequest;
import com.gacha.model.dto.user.RegistRequest;
import com.gacha.model.dto.user.UserDto;

public interface UserService {
	boolean regist(RegistRequest registRequest);

	UserDto login(LoginRequest loginRequest);

	FullUserInfo searchUserInfo(String userId);
}
