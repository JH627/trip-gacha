package com.gacha.model.service;

public interface MailService {
	
    /**
     * 이메일로 인증코드를 발송합니다.
     * 
     * @param to 보낼 이메일
     */
    void sendSimpleMessage(String to);

    /**
     * 인증코드를 확인합니다.
     * 
     * @param email 이메일
     * @param code 인증코드
     */
    void verifyCode(String email, String code);
}
