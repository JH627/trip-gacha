package com.gacha.model.dto.board;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateBoardRequest {
	@NotNull
    private Integer boardId;
    @NotEmpty
    private String title;
    @NotEmpty
    private String content;
}
