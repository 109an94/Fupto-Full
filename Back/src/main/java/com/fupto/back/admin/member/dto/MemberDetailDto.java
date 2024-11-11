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
public class MemberDetailDto {
    //회원 정보 목록
    private Long id;

    private String nickname; //이름
    private String userId; //닉네임
    private String password; //?필요할까?
    private String email; // 이메일
    private String tel; // 전화번호 ?000-0000-000으로 받아야함
    private String gender; // 성별 ?gender 선택필요
    private String role; // 등급
    //소셜로그인

    //활동 기록 목록
    private Instant createDate;
    private Instant updateDate;
        //작성글
    //게시글
    //덧글
    //자랑없음
    //문의없음
        //경고 여부
    //경고 횟수 (post필요)
    //제재여부

    //관심 목록
        //찜
        //알림

}
