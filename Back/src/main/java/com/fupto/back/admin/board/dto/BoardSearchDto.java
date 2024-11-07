package com.fupto.fupto.admin.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class BoardSearchDto {
    private Integer page;
    private Integer size;
    private String keyword;
    private List<Long> categoryIds;
}
