package com.fupto.back.admin.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FavoriteListDto {
    private Long id;

    private Long productId;
    private String productName;

    private Long memberId;
    private String memberName;

    private Instant createDate;

    private String productBrandName;
}
