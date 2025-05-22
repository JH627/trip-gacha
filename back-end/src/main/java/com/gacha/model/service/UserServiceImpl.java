package com.gacha.model.service;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gacha.model.dao.ImageDao;
import com.gacha.model.dao.UserDao;
import com.gacha.model.dto.enums.ImageCategory;
import com.gacha.model.dto.image.ImageDto;
import com.gacha.model.dto.user.FullUserInfo;
import com.gacha.model.dto.user.RegistRequest;
import com.gacha.model.dto.user.UpdateRequest;
import com.gacha.model.dto.user.UserDto;
import com.gacha.model.dto.user.UserStat;
import com.gacha.util.ImageUtil;
import com.gacha.util.PasswordUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final ImageUtil imageUtil;
    private final UserDao userDao;
    private final ImageDao imageDao;

    @Override
    @Transactional
    public boolean regist(RegistRequest registRequest) {
        String email = registRequest.getEmail();
        String hashingPassword = PasswordUtil.hash(registRequest.getPassword());
        String nickname = registRequest.getNickname();
        String profileImg = null;

        if (registRequest.getProfileImg() == null) {
            System.out.println("볐음");
        }

        if (registRequest.getProfileImg() != null) {
            profileImg = imageUtil.upload(registRequest.getProfileImg(), ImageCategory.profile);

            System.out.println(profileImg);
        }

        UserDto user = UserDto.builder()
                .email(email)
                .password(hashingPassword)
                .nickname(nickname)
                .profileImg(profileImg)
                .build();

        try {
            userDao.regist(user);

            if (profileImg != null) {
                ImageDto img = ImageDto.builder()
                        .userId(user.getUserId())
                        .imagePath(profileImg)
                        .category(ImageCategory.profile)
                        .build();

                imageDao.insert(img);
            }
        } catch (DuplicateKeyException e) {
            System.out.println("중복된 사용자 이메일");
            return false;
        }

        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public FullUserInfo searchUserInfo(String userId) {
        UserDto userDto = userDao.selectByUserId(Integer.parseInt(userId));

        if (userDto == null) {
            return null;
        }

        return new FullUserInfo(userDto.getEmail(), userDto.getNickname(), userDto.getProfileImg());
    }

    @Override
    @Transactional(readOnly = true)
    public UserStat getUserStats(Integer userId) {
        return userDao.selectUserStatsByUserId(userId);
    }

    @Override
    @Transactional
    public void updateProfile(Integer userId, UpdateRequest updateRequest) {
        String newNickname = updateRequest.getNickname();
        String profileImg = null;

        if (updateRequest.getProfileImg() != null) {
            profileImg = imageUtil.upload(updateRequest.getProfileImg(), ImageCategory.profile);
        }

        userDao.update(newNickname, profileImg, userId);

        if (profileImg != null) {
            ImageDto img = ImageDto.builder()
                    .userId(userId)
                    .imagePath(profileImg)
                    .category(ImageCategory.profile)
                    .build();

            imageDao.insert(img);
        }
    }
}
