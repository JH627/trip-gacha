package com.gacha.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class MailConfig {

    private final Dotenv dotenv = Dotenv.load();
    @Bean
    public JavaMailSender NaverMailService(){
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost("smtp.gmail.com"); 
        javaMailSender.setUsername(dotenv.get("MAIL_USERNAME"));
        javaMailSender.setPassword(dotenv.get("MAIL_PASSWORD"));

        javaMailSender.setPort(587); // SMTP 포트

        javaMailSender.setJavaMailProperties(getMailProperties()); // 메일 인증서버 가져오기

        return javaMailSender;
    }

    // 메일 인증서버 정보 가져오기
    private Properties getMailProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smtp"); // 프로토콜 설정
        properties.setProperty("mail.smtp.auth", "true"); // smtp 인증
        properties.setProperty("mail.smtp.starttls.enable", "true"); // smtp STARTTLS 사용
        properties.setProperty("mail.debug", "false"); // 디버그 사용
        properties.setProperty("mail.smtp.ssl.enable", "false"); // SSL 비활성화
        properties.setProperty("mail.smtp.ssl.trust", "smtp.gmail.com"); // SSL 인증 서버 (smtp 서버명)
    
        return properties;
    }
}
