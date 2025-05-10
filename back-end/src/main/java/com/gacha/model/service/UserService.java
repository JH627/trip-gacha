package com.gacha.model.service;

import com.gacha.model.dto.request.LoginRequest;
import com.gacha.model.dto.request.RegistRequest;
import com.gacha.model.dto.user.UserDto;

public interface UserService {
	boolean regist(RegistRequest registRequest);

	UserDto login(LoginRequest loginRequest);
}
