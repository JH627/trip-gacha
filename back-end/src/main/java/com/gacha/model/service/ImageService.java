package com.gacha.model.service;

import org.springframework.web.multipart.MultipartFile;

import com.gacha.model.dto.enums.ImageCategory;

public interface ImageService {
    /*
     * 파일 -> S3 서버 -> url 반환
     */
    String upload(MultipartFile file, ImageCategory imageCategor);
}
