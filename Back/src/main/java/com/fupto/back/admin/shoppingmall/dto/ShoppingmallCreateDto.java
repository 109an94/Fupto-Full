package com.fupto.back.admin.shoppingmall.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingmallCreateDto {

    private String korName;
    private String engName;
    private String url;
    private String description;
    private String img;
    private Integer deliveryfee;
    private Integer taxes;
    private Boolean active;
}
