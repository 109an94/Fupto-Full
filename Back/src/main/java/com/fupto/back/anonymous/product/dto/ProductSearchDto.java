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
public class ProductSearchDto {
    private Long gender;
    private List<Long> category;
    private List<Long> sub;
    private List<Long> brand;
    private List<Long> shoppingmall; //쇼핑몰 추가
    private Integer min;
    private Integer max;

    @Builder.Default
    private String sort = "popular";

    private Long cursor;

    @Builder.Default
    private Integer limit = 20;
}
