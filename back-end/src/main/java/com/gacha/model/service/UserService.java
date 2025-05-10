package com.gacha.model.service;

import com.gacha.model.dto.request.RegistRequest;

public interface UserService {
	boolean regist(RegistRequest registRequest);
}
