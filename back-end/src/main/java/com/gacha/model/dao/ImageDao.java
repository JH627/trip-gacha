package com.gacha.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.gacha.model.dto.image.ImageDto;

@Mapper
public interface ImageDao {
    void insert(ImageDto imageDto);
}
