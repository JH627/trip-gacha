package com.gacha.model.service;

import com.gacha.model.dto.user.FullUserInfo;
import com.gacha.model.dto.user.RegistRequest;

public interface UserService {
	boolean regist(RegistRequest registRequest);

	FullUserInfo searchUserInfo(String userId);
}
