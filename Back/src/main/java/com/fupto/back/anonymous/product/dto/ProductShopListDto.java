package com.fupto.back.anonymous.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductShopListDto {
    private Long id;
    private Long productId;
    private String shopName;
    private Integer price;
    private String productUrl;
    private String logoUrl;
    private Integer deliveryFee;
    private Integer taxes;
}
