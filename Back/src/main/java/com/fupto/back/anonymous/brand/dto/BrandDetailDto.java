package com.fupto.back.anonymous.brand.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrandDetailDto {
    private Long id;
    private String korName;
    private String engName;
    private String description;
    private String img;
}
