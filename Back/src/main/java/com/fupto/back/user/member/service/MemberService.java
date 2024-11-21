package com.fupto.back.user.member.service;

import com.fupto.back.user.member.dto.MemberEditDto;
import com.fupto.back.user.member.dto.MemberResponseDto;
import org.springframework.core.io.Resource;

import java.io.IOException;

public interface MemberService {
    MemberResponseDto editMember (String userId, MemberEditDto signupDto);
    MemberResponseDto getMember (Long id);
    Resource getProductImage(Long id) throws IOException;
}
