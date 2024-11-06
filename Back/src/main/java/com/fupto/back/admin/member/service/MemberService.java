package com.fupto.back.admin.member.service;

import com.fupto.back.admin.member.dto.MemberListDto;
import com.fupto.back.admin.member.dto.MemberResponseDto;
import com.fupto.back.admin.member.dto.MemberSearchDto;
import com.fupto.back.entity.Member;

import java.util.List;


public interface MemberService {
    List<Member> getList();
    MemberResponseDto getMemberList(MemberSearchDto memberSearchDto);
    MemberListDto getMemberById(Long id);
    List<MemberListDto> getMemberWithDetails();
}
