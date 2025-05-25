package com.socket.model.dto.game;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InviteRequest {
    private String planId;
    private List<String> userIds;
    private Game gameType;
}
