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
public class ProductDetailDto {
    private Long id;
    private String productName;
    private Integer retailPrice;
    private String description;
    private List<ProductCateDto> categories;
    private List<ProductImageDto> images;
    private String brandEngName;
    private ProductPriceInfoDto priceInfo;
    private List<ProductShopListDto> shops;
}
