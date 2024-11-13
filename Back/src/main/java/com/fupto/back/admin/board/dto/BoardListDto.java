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
public class BoardListDto {
    private Long id;
    private String title;
    private String contents;
    private String password;
    private Long regMemberId;
    private Instant createdAt;
    private Instant updatedAt;
    private Long boardCategoryId;
    private Boolean active;

}
