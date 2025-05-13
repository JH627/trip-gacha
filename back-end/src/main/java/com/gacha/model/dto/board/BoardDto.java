package com.gacha.model.dto.board;

import java.time.LocalDateTime;

import groovy.transform.builder.Builder;
import io.micrometer.common.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDto {
    private Integer boardId;
    private @NonNull Integer uploaderId;
    private @NonNull String title;
    private @NonNull String content;
    private @NonNull LocalDateTime createdAt;
    private @NonNull LocalDateTime updatedAt;
    private @NonNull LocalDateTime deletedAt;
    private @NonNull Boolean isDeleted;
}
