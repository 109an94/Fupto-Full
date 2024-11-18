package com.fupto.back.anonymous.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCateDto {
    private Long id;
    private String name;

    @Builder.Default
    private Integer level = null;
}