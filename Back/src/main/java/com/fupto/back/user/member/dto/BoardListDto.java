package com.fupto.back.user.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardListDto {
    private Long id;
    private String title;
    private String img;
    private Long regMemberId;
    private String regMemberNickName;
    private Instant createdAt;
    private Boolean active;
}
