package com.gacha.model.service;

import org.springframework.web.multipart.MultipartFile;

import com.gacha.model.dto.enums.ImageCategory;

public interface ImageService {
	
	/**
	 * 파일을 S3 서버에 업로드합니다.
	 * 
	 * @param file 업로드할 파일
	 * @param imageCategor 이미지 카테고리
	 * @return 업로드된 URL
	 */
    String upload(MultipartFile file, ImageCategory imageCategor);
}
