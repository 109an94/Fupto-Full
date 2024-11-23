package com.fupto.back.anonymous.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductListDto {
    private Long id;
    private String productName;
    private String brandEngName;
    private Integer salePrice;
    private String mainImageUrl;
    private String hoverImageUrl;
    private Long viewCount;
    private boolean isFavorite;
}
