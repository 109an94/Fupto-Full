package com.fupto.back.admin.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteResponseDto {
    private Long totalCount;
    private List<FavoriteListDto> favoriteList;
}
