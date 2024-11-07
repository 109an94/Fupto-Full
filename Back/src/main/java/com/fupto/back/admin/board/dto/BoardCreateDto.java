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
public class BoardCreateDto {

    private Long Id;
    private String title;
    private String content;
    private Instant createDate;

    private Long boardCategoryId;
    private Long createMemberId;

}
