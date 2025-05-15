package com.gacha.model.dto.user;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistRequest {
    private @NonNull String email;
    private @NonNull String nickname;
    private @NonNull String password;
    private MultipartFile profileImg;
}