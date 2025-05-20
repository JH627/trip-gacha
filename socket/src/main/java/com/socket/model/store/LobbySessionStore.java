package com.socket.model.store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.socket.model.dto.lobby.SocketUserInfo;

@Component
public class LobbySessionStore {
    private final Map<String, SocketUserInfo> users = new ConcurrentHashMap<>();

    public void add(String sessionId, SocketUserInfo user) {
        user.setSocketId(sessionId);
        users.put(sessionId, user);
    }

    public SocketUserInfo remove(String sessionId) {
        return users.remove(sessionId);
    }

    public List<SocketUserInfo> getAll() {
        return new ArrayList<>(users.values());
    }
}