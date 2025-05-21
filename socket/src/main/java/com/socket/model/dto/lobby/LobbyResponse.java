package com.socket.model.dto.lobby;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LobbyResponse<T> {
    private LobyEventType type;
    private T data;
}