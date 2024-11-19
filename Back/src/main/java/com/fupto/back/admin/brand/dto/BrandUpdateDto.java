package com.fupto.back.admin.brand.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrandUpdateDto {
    private Long id;
    private String korName;
    private String engName;
    private String url;
    private Boolean active;
    private String description;
    private String img;
}
