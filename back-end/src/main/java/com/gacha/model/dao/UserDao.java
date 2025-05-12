package com.gacha.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.gacha.model.dto.user.UserDto;

@Mapper
public interface UserDao {
    void regist(UserDto user);

    UserDto selectByEmail(String email);

    UserDto selectByUserId(int userId);
}
