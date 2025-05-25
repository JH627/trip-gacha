package com.socket.model.dto.room;

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
    private String startDate;
    private String endDate;
    private Integer destination;
}
