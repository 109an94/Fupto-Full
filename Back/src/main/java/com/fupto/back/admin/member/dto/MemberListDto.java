package com.fupto.back.admin.member.dto;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberListDto {
    private Long id;
    private String nickname;
    private String password;
    private String gender;
    private String tel;
    private String email;
    private Instant createDate;
    private Instant updateDate;

    private Instant loginDate;
    private Instant birthDate;
    private String userId;
    private String role;
    //private 소셜 로그인은 어떻게?
    //회원 유형 데이터 테이블은 어떻게? 권한설정?

}
