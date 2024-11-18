package com.fupto.back.admin.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardCreateDto {

    private String title;
    private String contents;
    private String active;
    private Long regMemberId;
    private String regMemberNickName;
    private Long boardCategoryId;
}
