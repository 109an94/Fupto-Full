package com.fupto.back.anonymous.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductBrandDto {
    private Long id;
    private String engName;
    private String korName;
}
