package com.fupto.back.admin.product.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRegDto {
    private Integer formId;
    private Boolean active;
    private Boolean presentId;
    private Long categoryId;
    private Long brandId;
    private Long shoppingMallId;
    private String url;
    private String productName;
    private Integer retailPrice;
    private Integer salePrice;
    private String description;

    private List<String> imageFileNames;
}
