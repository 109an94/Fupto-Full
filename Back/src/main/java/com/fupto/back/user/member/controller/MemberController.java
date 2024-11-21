package com.fupto.back.user.member.controller;

import com.fupto.back.auth.entity.FuptoUserDetails;
import com.fupto.back.user.member.dto.MemberEditDto;
import com.fupto.back.user.member.dto.MemberResponseDto;
import com.fupto.back.user.member.exception.InvalidPasswordException;
import com.fupto.back.user.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController("userMemberController")
@RequestMapping("/user/member")
public class MemberController {
    private MemberService memberService;

    public MemberController(MemberService memberService){
        this.memberService = memberService;
    }

    @PutMapping("edit")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MemberResponseDto> signUp(@AuthenticationPrincipal FuptoUserDetails userDetails,
                                                    @RequestBody MemberEditDto requestDto) {
        System.out.println("dto 확인용 : "+requestDto);
        System.out.println("details 확인용 : "+userDetails);
        try {
            MemberResponseDto editMember = memberService.editMember(userDetails.getUsername(), requestDto);
            System.out.println(editMember);
            return ResponseEntity.ok(editMember);
        }catch (InvalidPasswordException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<MemberResponseDto> getMember(@PathVariable Long id){
        System.out.println("getmember 실행 확인");
        return ResponseEntity.ok(memberService.getMember(id));
    }

}
