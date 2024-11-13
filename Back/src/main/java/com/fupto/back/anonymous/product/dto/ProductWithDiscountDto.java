package com.fupto.back.anonymous.product.dto;

import com.fupto.back.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductWithDiscountDto {
    private Product product;
    private Double discountRate;
}
