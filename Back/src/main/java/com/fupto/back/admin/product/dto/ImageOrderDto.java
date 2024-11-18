package com.fupto.back.admin.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageOrderDto {
    private Long id;
    private Boolean isNew;
    private Integer displayOrder;
}
