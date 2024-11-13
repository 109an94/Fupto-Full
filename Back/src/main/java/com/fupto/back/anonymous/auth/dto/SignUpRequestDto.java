package com.fupto.back.anonymous.auth.dto;

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
    private Instant createDate;
    private Instant updateDate;
    private Instant loginDate;
    private String role;

    public SignUpRequestDto(String userId, String nickname, String password) {
        this.userId = userId;
        this.nickname = nickname;
        this.password = password;

        this.createDate = Instant.now();
        this.updateDate = Instant.now();
    }
}
