package com.gacha.model.dto.board;

import com.gacha.model.dto.enums.BoardCategoey;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "제목은 필수 입력값입니다.")
    @Size(min = 1, max = 50, message = "제목은 1자 이상 50자 이하로 입력해주세요.")
    private @NonNull String title;
    
    @NotBlank(message = "내용은 필수 입력값입니다.")
    private @NonNull String content;
    
    @NotNull(message = "카테고리는 필수 입력값입니다.")
    private @NonNull BoardCategoey category;
}
