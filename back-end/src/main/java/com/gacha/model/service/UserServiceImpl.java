package com.gacha.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.gacha.model.dao.ImageDao;
import com.gacha.model.dao.UserDao;
import com.gacha.model.dto.enums.ImageCategory;
import com.gacha.model.dto.image.ImageDto;
import com.gacha.model.dto.user.FullUserInfo;
import com.gacha.model.dto.user.LoginRequest;
import com.gacha.model.dto.user.RegistRequest;
import com.gacha.model.dto.user.UserDto;
import com.gacha.util.ImageUtil;
import com.gacha.util.PasswordUtil;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private ImageUtil imageUtil;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ImageDao imageDao;

    @Override
    public boolean regist(RegistRequest registRequest) {    
        String email = registRequest.getEmail();
        String hashingPassword = PasswordUtil.hash(registRequest.getPassword());
        String nickname = registRequest.getNickname();
        String profileImg = null;

        if(registRequest.getProfileImg() == null){
            System.out.println("볐음");
        }
        
        if(registRequest.getProfileImg() != null){
            profileImg = imageUtil.upload(registRequest.getProfileImg(), ImageCategory.profile);

            System.out.println(profileImg);
        }

        UserDto user = UserDto.builder()
                                .email(email)
                                .password(hashingPassword)
                                .nickname(nickname)
                                .profileImg(profileImg)
                                .build();

        try{
            userDao.regist(user);

            if(profileImg != null){
                ImageDto img = ImageDto.builder()
                .userId(user.getUserId())
                .imagePath(profileImg)
                .category(ImageCategory.profile)
                .build();

                imageDao.insert(img);
            }
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
