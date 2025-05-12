package com.gacha.model.service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;
import jakarta.mail.internet.InternetAddress;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender emailSender;

    private Map<String, String> emailCodeMap = new HashMap<>();

    @Override
    public MimeMessage creatMessage(String to) throws MessagingException, UnsupportedEncodingException {
        System.out.println("메일받을 사용자" + to);
        System.out.println("인증번호" + emailCodeMap.get(to));

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
    }

    @Override
    public String createKey() {
            int leftLimit = 48; // numeral '0'
            int rightLimit = 122; // letter 'z'
            int targetStringLength = 10;
            Random random = new Random();
            String key = random.ints(leftLimit, rightLimit + 1)
                                           .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                                           .limit(targetStringLength)
                                           .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                                           .toString();
            System.out.println("생성된 랜덤 인증코드"+ key);
            return key;
    }

    @Override
    public void sendSimpleMessage(String to) throws Exception {

        emailCodeMap.put(to, createKey()); // 랜덤 인증코드 생성
        System.out.println("code :" + emailCodeMap.get(to));

        MimeMessage message = creatMessage(to); // "to" 로 메일 발송

        try { // 예외처리
            emailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean verifyCode(String email, String code) {
        if(!emailCodeMap.containsKey(email)){
            return false;
        }

        return emailCodeMap.get(email).equals(code);
    }
}
