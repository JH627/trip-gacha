package com.socket.model.dto.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class GameUserInfo {
    private String userId;
    private String nickname;
    private String img;
    private Integer clickTime;
}
