package com.gacha.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gacha.model.dao.ImageDao;
import com.gacha.model.dto.enums.ImageCategory;
import com.gacha.model.dto.image.ImageDto;
import com.gacha.model.dto.response.Response;
import com.gacha.model.service.ImageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;


@RestController
@RequestMapping("/img")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageDao imageDao;

    @PostMapping("/board")
    public ResponseEntity<Response<?>> uploadBoardImg(@RequestAttribute("userId") String userId, @RequestParam("img") MultipartFile file) {
        String url = imageService.upload(file, ImageCategory.board);
        
        ImageDto img = ImageDto.builder()
                .imagePath(url)
                .category(ImageCategory.board)
                .build();

        imageDao.insert(img);
        
        return ResponseEntity.ok(Response.onSuccess(url));
    }
}
