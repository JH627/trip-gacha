package com.socket.model.dto.room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomResponse<T> {
    private RoomEventType type;
    private boolean success;
    private T data;
}
