package com.socket.model.dto.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrocodileGamePlayer {
    private String userId;
    private String nickname;
    private String img;
    private boolean eliminated;
}
