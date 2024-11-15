package com.fupto.back.admin.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardSearchDto {

    private int page = 1;
    private int size = 3;
    private String sortBy = "createdAt";
    private String sortOrder = "desc";
    private String searchType;
    private String searchKeyWord;
    private String boardCategory;
    private Boolean active;
    private String dateType;
    private String startDate;
    private String endDate;
    private String quickDate;


}
