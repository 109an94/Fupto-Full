package com.fupto.back.anonymous.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SignUpRequestDto {
    private Long id;
    private String userId;
    private String nickname;
    private String password;
    private Instant birthDate;
    private String gender;
    private String tel;
    private String email;
    private String role = "ROLE_USER";
}
