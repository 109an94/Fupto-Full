package com.fupto.back.admin.product.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingMallSelectDto {
    private Long id;
    private String engName;
    private String korName;
}

