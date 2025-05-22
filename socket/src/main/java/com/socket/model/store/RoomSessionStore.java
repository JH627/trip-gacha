package com.socket.model.store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import com.socket.model.dto.room.SocketRoom;
import com.socket.model.dto.room.SocketRoomHeader;
import com.socket.model.dto.room.SocketRoomUser;

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
            roomHeaders.add(new SocketRoomHeader(room.getRoomId(), 
                                                room.getTitle(), 
                                                room.getDestination(),
                                                room.getUserList().size(), 
                                                room.getStartDate(), 
                                                room.getEndDate()));
        }

        return roomHeaders;
    }

    public String findUsersRoomId(String userId) {
        for (SocketRoom room : rooms.values()) {
            for(SocketRoomUser user : room.getUserList()){
                if(user.getUserId().equals(userId)){
                    return room.getRoomId();
                }
            }
        }

        return "";
    }

    public List<String> removeRoomsByOwnerUserId(String ownerUserId) {
        List<String> removedRoomIds = new ArrayList<>();

        rooms.entrySet().removeIf(entry -> {
            SocketRoom room = entry.getValue();
            if (room.getOwner() != null && ownerUserId.equals(room.getOwner().getUserId())) {
                removedRoomIds.add(room.getRoomId());
                return true;
            }
            return false;
        });

        return removedRoomIds;
    }

    public boolean removeUserFromRoom(String roomId, String userId) {
        SocketRoom room = rooms.get(roomId);
        if (room == null) return false;

        List<SocketRoomUser> userList = room.getUserList();
        return userList.removeIf(user -> user.getUserId().equals(userId));
    }

    public boolean isRoomOwner(String roomId, String userId){
        SocketRoom room = rooms.get(roomId);
        if (room == null) return false;

        return room.getOwner().getUserId().equals(userId);
    }

}
