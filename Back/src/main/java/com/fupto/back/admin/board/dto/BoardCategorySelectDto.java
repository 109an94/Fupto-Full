package com.fupto.back.admin.board.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardCategorySelectDto {
    private Long id;
    private String name;
}
