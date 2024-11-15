package com.fupto.back.admin.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BoardDetailDto {

    private Long id;
    private String title;
    private String contents;
    private String regMemberNickName;
    private Instant createdAt;
    private Instant modifiedAt;
    private String boardCategoryName;
    private Boolean active;
}
