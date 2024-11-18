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
public class ProductUpdateDto {
    private Long id;
    private String productName;
    private Integer retailPrice;
    private String url;
    private String description;
    private Boolean presentId;
    private Long mappingId;
    private Boolean active;

    private Long category1Id;
    private Long category2Id;
    private Long category3Id;
    private Long brandId;
    private Long shoppingMallId;
    private Integer salePrice;

    private List<ProductImageDto> productImages;
}
