package com.fupto.back.admin.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardUpdateDto {

    private Long id;
    private String title;
    private String contents;
    private Boolean active;
    private Long boardCategoryId;
    private Long regMemberId;
    private String img;

}
