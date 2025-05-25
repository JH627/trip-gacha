package com.socket.model.dto.room;

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
    private Integer destination;
    private Integer userCount;
    private String startDate;
    private String endDate;
}
