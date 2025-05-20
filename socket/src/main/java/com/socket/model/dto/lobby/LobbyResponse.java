package com.socket.model.dto.lobby;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LobbyResponse {
    private LobyEventType type;
    private List<SocketUserInfo> userInfos;
}