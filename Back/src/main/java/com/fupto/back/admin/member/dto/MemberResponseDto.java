package com.fupto.back.admin.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberResponseDto {
    private Long totalCount;
    private Long totalPages;
    private Boolean hasNextPage;
    private Boolean hasPrevPage;

    private List<Long> pages;
    private List<MemberListDto> members;
}
