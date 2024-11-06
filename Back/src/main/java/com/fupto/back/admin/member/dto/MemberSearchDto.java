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
    private String type;
    private String keyWord;

    public MemberSearchDto() {
        this.page = 1;
        this.size = 10;
        this.type = "userId";
        this.keyWord = "";

    }
}
