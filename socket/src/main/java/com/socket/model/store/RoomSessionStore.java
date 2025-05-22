package com.socket.model.store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import com.socket.model.dto.room.SocketRoom;
import com.socket.model.dto.room.SocketRoomHeader;

@Component
public class RoomSessionStore {
    private final Map<String, SocketRoom> rooms = new ConcurrentHashMap<>();

    public void add(String roomId, SocketRoom room) {
        room.setRoomId(roomId);;
        rooms.put(roomId, room);
    }

    public SocketRoom remove(String roomId) {
        return rooms.remove(roomId);
    }

    public SocketRoom get(String roomId){
        return rooms.get(roomId);
    }

    public List<SocketRoomHeader> getAll() {
        List<SocketRoom> roomList = new ArrayList<>(rooms.values());
        List<SocketRoomHeader> roomHeaders = new ArrayList<>();

        for(SocketRoom room : roomList){
            int userCount = room.getUserList().size() + 1;
            roomHeaders.add(new SocketRoomHeader(room.getRoomId(), 
                                                room.getTitle(), 
                                                room.getDestination(),
                                                userCount, 
                                                room.getStartDate(), 
                                                room.getEndDate()));
        }

        return roomHeaders;
    }
}
