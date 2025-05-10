package com.gacha.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.gacha.model.dao.UserDao;
import com.gacha.model.dto.request.LoginRequest;
import com.gacha.model.dto.request.RegistRequest;
import com.gacha.model.dto.user.FullUserInfo;
import com.gacha.model.dto.user.UserDto;
import com.gacha.util.PasswordUtil;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Override
    public boolean regist(RegistRequest registRequest) {
        String email = registRequest.getEmail();

        String password = registRequest.getPassword();
        String hashingPassword = PasswordUtil.hash(password);

        String nickname = registRequest.getNickname();
        // TODO : 이미지 파일 S3에 올리기 -> 반환 url 저장
        String profileImg = "";

        try{
            userDao.regist(email, hashingPassword, nickname, profileImg);
        } catch(DuplicateKeyException e){
            System.out.println("중복된 사용자 이메일");
            return false;
        }

        return true;
    }

    @Override
    public UserDto login(LoginRequest loginRequest) {
        // 이메일로 사용자 가져옴
        UserDto findUser = userDao.selectByEmail(loginRequest.getEmail());

        if(findUser==null){
            return null;
        }

        // 비밀번호 일치 확인
        if(!PasswordUtil.isMatch(loginRequest.getPassword(), findUser.getPassword())){
            return null;
        }

        return findUser;
    }

    @Override
    public FullUserInfo searchUserInfo(String userId) {
        UserDto userDto = userDao.selectByUserId(Integer.parseInt(userId));

        if(userDto == null){
            return null;
        }

        return new FullUserInfo(userDto.getEmail(), userDto.getNickname(), userDto.getProfileImg());
    }
}
