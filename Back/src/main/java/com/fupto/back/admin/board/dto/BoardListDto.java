package com.fupto.back.admin.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardListDto {
    private Long id;
    private String title;
    private String type;
    private Instant createDate;
    private Boolean active;

    private String boardCategoryName;
    private String regMemberNickName;

    private String boardImagePath;
}
