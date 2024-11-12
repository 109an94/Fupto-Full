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
    private Integer page = 1;
    private Integer size = 1;
    private String sort = "createDate";
    private String boardCategoryName; // 공지사항, 커뮤니티, FAQ, 고객센터
    private Boolean active;
    private String searchType; // 제목,작성자, 내용
    private String searchKeyWord;
//    private String dateType;
//    private String startDate;
//    private String endDate;


}
