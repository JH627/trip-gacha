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
public class SocketRoomHeader {
    private String roomId;
    private String title;
    private String tripTarget;
    private Integer userCount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
