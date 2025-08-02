package com.fupto.back.admin.shoppingmall.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingmallListDto {

    private Long id;
    private String korName;
    private String engName;
    private String url;
    private String description;
    private String img;
    private Integer deliveryfee;
    private Integer taxes;
    private Boolean active;
    private Boolean state;
    private Instant createDate;
    private Instant updateDate;
}
