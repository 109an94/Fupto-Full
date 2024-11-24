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
    private Long mappingId;
    private String productName;
    private String brandEngName;
    private Integer salePrice;
    private String mainImageUrl;
    private String hoverImageUrl;
    private Long viewCount;

    @Builder.Default
    private boolean isFavorite = false;
}
