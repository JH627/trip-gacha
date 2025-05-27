package com.socket.model.dto.room;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SocketRoom {
    private String roomId;
    private String title;
    private String password;
    private String startDate;
    private String endDate;
    private Integer destination;
    private SocketRoomUser owner;
    private boolean isPlanning;
    private List<SocketRoomUser> userList;
}
