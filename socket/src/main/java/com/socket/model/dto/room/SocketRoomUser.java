package com.socket.model.dto.room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SocketRoomUser {
    private String userId;
    private String nickname;
    private String img;
}
