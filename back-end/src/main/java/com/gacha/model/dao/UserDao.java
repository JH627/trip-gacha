package com.gacha.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.gacha.model.dto.user.UserDto;

@Mapper
public interface UserDao {
    void regist(String email, String password, String nickname, String profile_img);

    UserDto selectByEmail(String email);
}
