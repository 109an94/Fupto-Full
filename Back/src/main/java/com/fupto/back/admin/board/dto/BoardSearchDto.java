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
    private Integer page = 1;
    private Integer size = 1;
    private String sort = "create_time";
    private String order = "desc";
    private Boolean active;

//    private String boardCategoryName;
    private String searchKeyword; //
    private String searchType;    // 제목, 글쓴이, 내용

}
