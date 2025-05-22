package com.gacha.model.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.gacha.model.dto.user.UserDto;
import com.gacha.model.dto.user.UserStat;

@Mapper
public interface UserDao {
    void regist(UserDto user);

    UserDto selectByEmail(String email);

    UserDto selectByUserId(int userId);

    UserStat selectUserStatsByUserId(Integer userId);

    void update(@Param("nickname") String nickname, @Param("profileImg") String profileImg,
            @Param("userId") Integer userId);
}
