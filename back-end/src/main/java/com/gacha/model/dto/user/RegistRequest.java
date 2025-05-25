package com.gacha.model.dto.user;

import org.springframework.web.multipart.MultipartFile;

import com.gacha.model.dto.validation.annotation.ValidPassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private @NonNull String email;
    
    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Size(min = 2, max = 30, message = "닉네임은 2자 이상 30자 이하로 입력해주세요.")
    private @NonNull String nickname;
    
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @ValidPassword
    private @NonNull String password;
    
    private MultipartFile profileImg;
}