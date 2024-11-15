package com.fupto.back.admin.shoppingmall.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingmallResponseDto {

    private Long totalElements;
    private Long totalPages;
    private Boolean hasNextPage;
    private Boolean hasPreviousPage;
    private List<Long> pages;
    private List<ShoppingmallListDto> shoppingmalls;
}
