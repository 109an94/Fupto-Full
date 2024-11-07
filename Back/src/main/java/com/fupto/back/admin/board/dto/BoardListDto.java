package com.fupto.fupto.admin.board.dto;

import lombok.*;

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
