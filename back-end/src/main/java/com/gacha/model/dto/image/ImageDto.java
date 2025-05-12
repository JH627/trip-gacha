package com.gacha.model.dto.image;

import com.gacha.model.dto.enums.ImageCategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageDto {
    private Integer imgId;
    private Integer userId;
    private String imagePath;
    private ImageCategory category;
}