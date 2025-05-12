package com.gacha.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.gacha.model.dto.enums.ImageCategory;
import com.gacha.util.ImageUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    @Autowired
    private ImageUtil imageutil;

    @Override
    public String upload(MultipartFile file, ImageCategory imageCategory){
        return imageutil.upload(file, imageCategory);
    }
}
