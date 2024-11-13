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
public class BoardCreateDto {
    private Long id;
    private String title;
    private String contents;
    private String password;
    private String regMemberName;
    private Instant createdAt;
    private Instant updatedAt;
    private String boardCategoryName;
    private Boolean active = true;
}
