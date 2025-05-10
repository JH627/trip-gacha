package com.gacha.model.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao {
    void regist(String email, String password, String nickname, String profile_img);
}
