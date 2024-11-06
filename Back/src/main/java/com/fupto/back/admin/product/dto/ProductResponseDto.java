package com.fupto.back.admin.product.dto;

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
    private Integer pageSize;
    private Integer currentPage;
    private long totalPages;
    private long totalElements;
    private List<ProductListDto> products;
}
