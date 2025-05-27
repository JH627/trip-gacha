package com.socket.model.store;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import com.socket.model.dto.room.SocketRoom;
import com.socket.model.dto.room.SocketRoomHeader;
import com.socket.model.dto.room.SocketRoomUser;

@Component
public class RoomSessionStore {
    private final Map<String, SocketRoom> rooms = new ConcurrentHashMap<>();

    private final String[] startDates = new String[]{ "2025-03-01", "2025-04-01", "2025-07-25", "2025-12-21" };
    private final String[] endDates = new String[]{ "2025-03-03", "2025-04-01", "2025-07-30", "2025-12-24" };
    private final String[] titles = new String[]{ "혼저옵서예", "만우절 기념 여행", "드가자", "남정내들끼리가는칙칙한여행" };
    private final String[] destinations = new String[]{ "39", "31", "37", "6" };
    private final int[] userCounts = new int[]{ 2, 1, 4, 6};

    public void initTestData(){

        if(rooms.values().size() != 0){
            return;
        }

        for(int i = 0; i < 4; i++){
            String roomId = UUID.randomUUID().toString();
            String password = "123";

            SocketRoomUser testUser = new SocketRoomUser("","","");

            List<SocketRoomUser> testUserList = new ArrayList<>();

            for(int j = 0; j < userCounts[i]; j++){
                testUserList.add(testUser);
            }

            SocketRoom sr = SocketRoom
                            .builder()
                            .roomId(roomId)
                            .password(password)
                            .startDate(startDates[i])
                            .endDate(endDates[i])
                            .title(titles[i])
                            .destination(Integer.parseInt(destinations[i]))
                            .userList(testUserList)
                            .createdAt(LocalDateTime.now())
                            .build();
            
            rooms.put(roomId, sr);
        }
    }

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
                                                            room.getEndDate(),
                                                            room.getCreatedAt()
                                                        )
                                                            
            );
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

    public void changePlanState(String roomId){
        SocketRoom room = rooms.get(roomId);
        room.setPlanning(true);

        rooms.put(roomId, room);
    }

}
