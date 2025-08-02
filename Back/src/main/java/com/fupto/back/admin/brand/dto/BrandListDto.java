package com.fupto.back.admin.brand.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrandListDto {

    private Long id;
    private String korName;
    private String engName;
    private String url;
    private String description;
    private String img;
    private Boolean active;
    private Boolean state;
    private Instant createDate;
    private Instant updateDate;

}
