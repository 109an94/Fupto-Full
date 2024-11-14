package com.fupto.back.admin.member.controller;

import com.fupto.back.admin.member.dto.MemberDetailDto;
import com.fupto.back.admin.member.dto.MemberListDto;
import com.fupto.back.admin.member.dto.MemberResponseDto;
import com.fupto.back.admin.member.dto.MemberSearchDto;
import com.fupto.back.admin.member.service.MemberService;
import com.fupto.back.entity.Member;
import com.fupto.back.repository.MemberRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminMemberController")
@RequestMapping("admin/members")
public class MemberController{
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    public MemberController(MemberService memberService, MemberRepository memberRepository) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
    }

    @GetMapping
    public List<MemberListDto> getAllMembers(){
        return memberService.getMemberWithDetails();
    }

    @GetMapping("/all")
    public List<Member> getMemberList(){
        return memberRepository.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<MemberDetailDto> getMemberById(@PathVariable Long id){
        return ResponseEntity.ok(memberService.getMemberById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<MemberResponseDto> getMembers(@ModelAttribute MemberSearchDto memberSearchDto){
        return ResponseEntity.ok(memberService.getMemberList(memberSearchDto));
    }

}
