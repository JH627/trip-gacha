package com.gacha.model.service;

import com.gacha.model.dto.user.LoginRequest;
import com.gacha.model.dto.user.LoginResponse;

public interface AuthService {
	LoginResponse login(LoginRequest loginRequest);
	
	void logout(String authorization, String refreshToken);

    String reGenerateAccessToken(String refreshToken);

    boolean isBlacklisted(String token);
}
