package com.gacha.model.dto.user;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRequest {
    @Size(min = 2, max = 30, message = "닉네임은 2자 이상 30자 이하로 입력해주세요.")
    private String nickname;
    
    private MultipartFile profileImg;
}
