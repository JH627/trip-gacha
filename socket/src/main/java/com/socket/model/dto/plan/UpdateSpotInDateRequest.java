package com.socket.model.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UpdateSpotInDateRequest {
    private String type;
    private String date;
    private Integer spotId;
}
