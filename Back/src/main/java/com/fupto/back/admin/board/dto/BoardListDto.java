package com.fupto.back.admin.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardListDto {

    private Long id;
    private String title;
    private String content;
    private Instant createDate;

    private String boardCategoryName;
    private String regMemberNickName;

}
