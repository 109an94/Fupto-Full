package com.fupto.back.anonymous.auth.service;

import com.fupto.back.anonymous.auth.dto.SignUpRequestDto;
import com.fupto.back.entity.Member;
import com.fupto.back.repository.MemberRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class DefualtAuthService implements AuthService {

    private MemberRepository memberRepository;
    private ModelMapper modelMapper;

    public DefualtAuthService(MemberRepository memberRepository, ModelMapper modelMapper) {
        this.memberRepository = memberRepository;
        this.modelMapper = modelMapper;
    }

    public Member regMember (SignUpRequestDto signupDto){
        Member member = modelMapper.map(signupDto, Member.class);
//        member.setUserId(signupDto.getUserId());
        member.setPassword(signupDto.getPassword());
        member.setNickname(signupDto.getNickname());

        member.setPassword(signupDto.getPassword());
        System.out.println(member);
        return null;
    }


}
