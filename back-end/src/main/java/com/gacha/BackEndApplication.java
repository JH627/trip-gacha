package com.gacha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class BackEndApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();

		System.setProperty("S3_BUCKET_NAME", dotenv.get("S3_BUCKET_NAME"));
		System.setProperty("S3_ACCESS_KEY", dotenv.get("S3_ACCESS_KEY"));
		System.setProperty("S3_SECRET_KEY", dotenv.get("S3_SECRET_KEY"));
		System.setProperty("S3_REGION", dotenv.get("S3_REGION"));
		System.setProperty("OPEN_API_KEY", dotenv.get("OPEN_API_KEY"));

		SpringApplication.run(BackEndApplication.class, args);
	}

}
