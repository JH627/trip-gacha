package com.socket.model.dto.lobby;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SocketUserInfo {
    private String socketId;
    private String nickname;
    private String img;
    private Long loginTime;
}