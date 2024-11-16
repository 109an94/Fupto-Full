package com.fupto.back.anonymous.product.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPriceInfoDto {
    private Integer retailPrice;
    private Integer salePrice;
    private Integer discountRate;
}
