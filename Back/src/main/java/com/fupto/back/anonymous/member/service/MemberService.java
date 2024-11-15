package com.fupto.back.anonymous.member.service;

import com.fupto.back.anonymous.member.dto.SignUpRequestDto;
import com.fupto.back.entity.Member;

public interface MemberService {
    SignUpRequestDto regMember (SignUpRequestDto signupDto);
}
