package com.gacha.model.dto.board;


import com.gacha.model.dto.enums.BoardCategoey;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddBoardRequest {
    private @NonNull String title;
    private @NonNull String content;
    private @NonNull BoardCategoey category;
}
