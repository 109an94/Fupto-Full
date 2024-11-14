package com.fupto.back.anonymous.member.service;

import com.fupto.back.anonymous.member.dto.SignUpRequestDto;
import com.fupto.back.entity.Member;
import com.fupto.back.repository.MemberRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class DefualtMemberService implements MemberService {

    private MemberRepository memberRepository;
    private ModelMapper modelMapper;
//    private Passwor

    public DefualtMemberService(MemberRepository memberRepository, ModelMapper modelMapper) {
        this.memberRepository = memberRepository;
        this.modelMapper = modelMapper;
    }

    public SignUpRequestDto regMember (SignUpRequestDto dto){
//        Member member = Member.builder()
//                .userId(dto.getUserId())
//                .nickname(dto.getNickname())
//                .password(dto.getPassword())
//                .birthDate(dto.getBirthDate())
//                .gender(dto.getGender())
//                .tel(dto.getTel())
//                .email(dto.getEmail())
//                .role(dto.getRole())
//                .build();
        Member member = modelMapper.map(dto, Member.class);
//        member.setPassword();

        Member saveMember = memberRepository.save(member);



        return modelMapper.map(saveMember, SignUpRequestDto.class);
    }


}
