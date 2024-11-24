package com.fupto.back.admin.board.dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardCreateDto {
    //    private Long id;
    private String title;
    private String contents;
    private Long regMemberId;
    //    private String regMemberNickName;
//    private Instant createdAt;
//    private Instant modifiedAt;
    private Long boardCategoryId;
    //    private String boardCategoryName;
    private Boolean active;
    private String img;
}
