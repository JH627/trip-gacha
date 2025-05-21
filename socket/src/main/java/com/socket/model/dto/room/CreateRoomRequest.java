package com.socket.model.dto.room;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreateRoomRequest {
    private String title;
    private String password;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String tripTarget;
    private SocketRoomUser owner;
}
