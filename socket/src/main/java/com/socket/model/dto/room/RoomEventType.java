package com.socket.model.dto.room;

public enum RoomEventType {
    CREATE, // 내가 만듬
    CREATED, // 다른 사람이 만듬
    JOIN, // 입장
    INIT, // 처음 접근함
    LEAVE, // 떠남
    BOOM, // 방이 터짐
    PLAN // 여행 짜기 시작
}
