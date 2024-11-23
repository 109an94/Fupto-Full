package com.fupto.back.user.member.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AlertPriceDto {
    private Long id;
    private Integer alertPrice;
}
