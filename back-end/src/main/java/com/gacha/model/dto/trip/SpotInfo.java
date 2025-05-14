package com.gacha.model.dto.trip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 관광지 정보
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SpotInfo {
    private Integer spotId;
    private String destination;
    private String name;
    private String content;
    private String img;
    private String address;
    private Integer likes;
    private Integer stars;
    private String category;
    private String phone;
    private String workTime;
    private Boolean marked;
}
