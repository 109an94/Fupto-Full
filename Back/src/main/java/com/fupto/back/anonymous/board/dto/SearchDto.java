package com.fupto.back.anonymous.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchDto {

    private int page = 1;
    private int size = 15;
    private String sortBy = "createdAt";
    private String sortOrder = "desc";
    private String searchType;
    private String searchKeyWord;
    private String boardCategory;
    private Boolean active;

}
