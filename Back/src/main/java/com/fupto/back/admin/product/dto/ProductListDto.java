package com.fupto.back.admin.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductListDto {
    private Long id;
    private String productName;
    private Integer retailPrice;
    private String url;
    private String description;
    private Boolean presentId;
    private Long mappingId;
    private Boolean active;
    private Boolean state;
    private Instant createDate;
    private Instant updateDate;

    private String brandEngName;
    private String categoryName;
    private String shoppingMallEngName;
    private Integer salePrice;

    private String imagePath;
}
