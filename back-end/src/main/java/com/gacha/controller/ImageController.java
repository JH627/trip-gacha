package com.gacha.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gacha.global.api.Response;
import com.gacha.global.jwt.annotation.LoginUser;
import com.gacha.model.dao.ImageDao;
import com.gacha.model.dto.enums.ImageCategory;
import com.gacha.model.dto.image.ImageDto;
import com.gacha.model.service.ImageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "게시판 이미지 API")
@RestController
@RequestMapping("/img")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;
    private final ImageDao imageDao;

    @Operation(summary = "게시판 이미지 등록 API", description = "게시판에서 사용할 이미지를 등록하고, 등록된 URL을 반환합니다.")
    @PostMapping("/board")
    public Response<?> uploadBoardImg(@LoginUser Integer userId, @RequestParam("img") MultipartFile file) {
        String url = imageService.upload(file, ImageCategory.board);
        
        ImageDto img = ImageDto.builder()
                .imagePath(url)
                .category(ImageCategory.board)
                .build();

        imageDao.insert(img);
        
        return Response.onSuccess(url);
    }
}
