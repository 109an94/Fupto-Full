package com.fupto.back.admin.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSearchDto {
    private Integer page = 0;
    private Integer size = 10;
    private String sort = "id";
    private Boolean presentId = true;
    private Boolean state = true;
    private Long category1;
    private Long category2;
    private Long category3;
    private String searchType;
    private String searchKeyword;
    private Boolean active;
    private Integer minPrice;
    private Integer maxPrice;
    private String dateType;
    private Instant startDate;
    private Instant endDate;
}
