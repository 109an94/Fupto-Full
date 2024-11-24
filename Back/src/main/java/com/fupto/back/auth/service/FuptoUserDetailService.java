package com.fupto.back.auth.service;

import com.fupto.back.anonymous.member.dto.SignUpRequestDto;
import com.fupto.back.auth.entity.FuptoUserDetails;
import com.fupto.back.auth.exception.UserAlreadyExistsException;
import com.fupto.back.entity.Member;
import com.fupto.back.repository.MemberRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class FuptoUserDetailService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private MemberRepository memberRepository;

    public FuptoUserDetailService(MemberRepository memberRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Member member = memberRepository.findByUserId(userId);
        if (member == null){
            throw new UsernameNotFoundException(userId+" User not found");
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();

        String role = member.getRole();
        authorities.add(new SimpleGrantedAuthority(role));
        System.out.println("토큰 발급했는 지 확인용----------------");
        return FuptoUserDetails.builder()
                .id(member.getId())
                .username(userId)
//                .password(passwordEncoder.encode(member.getPassword()))
                .password(member.getPassword())
                .email(member.getEmail())
                .authorities(authorities)
                .build();
    }
    @Transactional
    public void updateLoginDate (String userId){
        Member member = memberRepository.findByUserId(userId);
        if (member != null){
            member.setLoginDate(Instant.now().plusSeconds(32400));
            memberRepository.save(member);
        }
    }


    public FuptoUserDetails regNewUser(SignUpRequestDto requestDto)
            throws UserAlreadyExistsException {
        if (memberRepository.existsByUserId(requestDto.getUserId())) {
            throw new UserAlreadyExistsException("your Id is already exists");
        }
        if (memberRepository.existsByEmail(requestDto.getEmail())) {
            throw new UserAlreadyExistsException("your Email is already exists");
        }
        System.out.println("서비스 전달 확인 :"+requestDto);
        //비밀번호 암호화
        requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        System.out.println("암호화 확인 :"+requestDto.getPassword());

        //dto를 entity로 변환 후 저장
        Member member = modelMapper.map(requestDto, Member.class);
        Member savemember = memberRepository.save(member);
        System.out.println("entity 확인 : "+member.getRole());;

        //userDetail로 변환
        FuptoUserDetails userDetails = modelMapper.map(savemember, FuptoUserDetails.class);
        userDetails.setAuthorities(Collections.singletonList(new SimpleGrantedAuthority(savemember.getRole())));
        System.out.println("권한 확인:"+userDetails.getAuthorities());

        return userDetails;
    }
}