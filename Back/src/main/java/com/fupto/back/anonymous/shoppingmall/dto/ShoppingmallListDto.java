package com.fupto.back.anonymous.shoppingmall.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShoppingmallListDto {

    private Long id;
    private String korName;
    private String engName;
}
