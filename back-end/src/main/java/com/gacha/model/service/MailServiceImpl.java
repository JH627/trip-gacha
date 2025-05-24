package com.gacha.model.service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.gacha.exception.MailErrorCode;
import com.gacha.exception.MailException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.mail.internet.InternetAddress;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender emailSender;

    private Map<String, String> emailCodeMap = new HashMap<>();

    @Override
    public void sendSimpleMessage(String to) {
        emailCodeMap.put(to, createKey()); // 랜덤 키 생성
        
        MimeMessage message = creatMessage(to); // "to" 로 메일 발송
        emailSender.send(message);
    }
    
    @Override
    public void verifyCode(String email, String code) {
        if (!emailCodeMap.containsKey(email)){
            throw new MailException(MailErrorCode.INVALID_AUTH_CODE);
        }
    }
    
    private String createKey() {
            int leftLimit = 48; // numeral '0'
            int rightLimit = 122; // letter 'z'
            int targetStringLength = 10;
            Random random = new Random();
            String key = random.ints(leftLimit, rightLimit + 1)
                                           .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                                           .limit(targetStringLength)
                                           .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                                           .toString();
            
            log.info("생성된 랜덤 인증코드:{}", key);
            return key;
    }
    
    private MimeMessage creatMessage(String to) {
        System.out.println("메일받을 사용자" + to);
        System.out.println("인증번호" + emailCodeMap.get(to));

        try {
        	MimeMessage message = emailSender.createMimeMessage();

	        message.addRecipients(RecipientType.TO, to); // 메일 받을 사용자
	        message.setSubject("[Fligent] 비밀번호 변경을 위한 이메일 인증코드 입니다"); // 이메일 제목
	
	        StringBuilder msgBody = new StringBuilder();
	        msgBody.append("<h1>안녕하세요</h1>");
	        msgBody.append("<h1>여행 계획부터 여행의 시작이다! 계획을 즐겁게 만들어주는 TripGacha입니다!</h1>");
	        msgBody.append("<br>");
	        msgBody.append("<p>아래 인증 코드를 사이트에 입력해주세요</p>");
	        msgBody.append("<br>");
	        msgBody.append("<br>");
	        msgBody.append("<div align='center' style='border:1px solid black'>");
	        msgBody.append("<h3 style='color:blue'>회원가입 인증코드 입니다</h3>");
	        msgBody.append("<div style='font-size:130%'>");
	        msgBody.append("<strong>");
	        msgBody.append(emailCodeMap.get(to));
	        msgBody.append("</strong></div><br/></div>");
	
	        message.setText(msgBody.toString(), "utf-8", "html"); // 메일 내용, charset타입, subtype
	
	        message.setFrom(new InternetAddress("TripGacha@tripGacha.com", "TripGacha"));
	        return message;
        } catch (UnsupportedEncodingException e) {
        	log.info("이메일 발신자 정보를 설정하는 중 오류가 발생했습니다.");
            throw new MailException(MailErrorCode.MAIL_SEND_FAILED);
        } catch (MessagingException e) {
        	log.info("메일 생성 중 오류가 발생했습니다.");
            throw new MailException(MailErrorCode.MAIL_SEND_FAILED);
        }
        
    }
}
