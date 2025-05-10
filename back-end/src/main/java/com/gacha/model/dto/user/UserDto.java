package com.gacha.model.dto.user;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private @NonNull Integer userId;
    private @NonNull String email;
    private @NonNull String password;
    private @NonNull String nickname;
    private String profileImg;
    private @NonNull Byte role;
    private @NonNull LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Boolean isDelete;
}
