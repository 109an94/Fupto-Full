package com.fupto.back.admin.board.dto;

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
//    private String searchKeyword;
//    private String searchType;
//    private List<Long> categoryName;
    private String boardCategoryName;
}
