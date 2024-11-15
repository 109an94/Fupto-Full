package com.fupto.back.anonymous.member.controller;

import com.fupto.back.anonymous.member.dto.SignUpRequestDto;
import com.fupto.back.anonymous.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
public class MemberController {
    private MemberService memberService;

    public MemberController(MemberService memberService){
        this.memberService = memberService;
    }

    @PostMapping("signup")
    public ResponseEntity<SignUpRequestDto> signUp(@RequestBody SignUpRequestDto requestDto) {

        return ResponseEntity.ok(memberService.regMember(requestDto));
    }

}
