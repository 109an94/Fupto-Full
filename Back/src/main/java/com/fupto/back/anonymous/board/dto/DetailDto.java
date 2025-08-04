package com.fupto.back.anonymous.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetailDto {
    private Long id;
    private String title;
    private String contents;
    private String img;
    private String regMemberNickName;
    private Long regMemberId;
    private Instant createdAt;
    private Instant modifiedAt;
    private String boardCategoryName;

}
