package com.fupto.back.anonymous.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDto {
    private List<ProductListDto> products;
    private Long nextCursor; // 다음 페이지 첫 상품의 ID
    private Boolean hasMore; // 다음 페이지 존재 여부
//    private Long totalCount; // 전체 검색 결과 수
}
