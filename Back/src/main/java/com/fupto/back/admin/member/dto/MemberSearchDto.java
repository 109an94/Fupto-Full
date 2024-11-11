package com.fupto.back.admin.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
public class MemberSearchDto {
    private Integer page;
    private Integer size;
    private String memberType;
    private String gender;
    private String searchType;
    private String searchKeyWord;

    public MemberSearchDto() {
        this.page = 1;
        this.size = 10;
        this.searchType = "userId";
        this.searchKeyWord = "";

    }
}
