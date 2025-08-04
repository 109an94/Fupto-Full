package com.fupto.back.user.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MemberEditDto {
//    private Long id; //전역에서 받아옴
//    private String userId; //전역에서 받아옴



//    private String gender; //변경 불가
    private String password;

    private String newPassword;
    private String confirmPassword;

    private String nickname;
    private String birthDate;
    private String email;
    private Instant updateDate = Instant.now().plusSeconds(32400);
//    private String tel;
//    private String role = "ROLE_USER";
}
