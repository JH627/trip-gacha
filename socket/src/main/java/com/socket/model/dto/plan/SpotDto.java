package com.socket.model.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SpotDto {
    private int spotId;
    private String name;
    private String address;
    private String category;
    private String content;
    private String destination;
    private String img;
    private double latitude;
    private double longitude;
    private int likes;
    private boolean marked;
    private String phone;
    private int stars;
    private String workTime;
}