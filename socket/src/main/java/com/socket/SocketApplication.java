package com.socket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class SocketApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
				.directory("./") // .env 파일 경로 설정 (기본: 프로젝트 루트)
				.load();

		// 환경변수를 시스템 프로퍼티에 추가
		dotenv.entries().forEach(entry ->
									System.setProperty(entry.getKey(), entry.getValue())
								);

		SpringApplication.run(SocketApplication.class, args);
	}

}
