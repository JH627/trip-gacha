package com.gacha.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.gacha.model.dao.UserDao;
import com.gacha.model.dto.request.RegistRequest;
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
}
