package com.fupto.back.auth.service;

import com.fupto.back.auth.entity.FuptoUserDetails;
import com.fupto.back.entity.Member;
import com.fupto.back.repository.MemberRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FuptoUserDetailService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private MemberRepository memberRepository;

public FuptoUserDetailService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
    this.memberRepository = memberRepository;
    this.passwordEncoder = passwordEncoder;
}

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
//        if (username.equals("admin")){}
        Member member = memberRepository.findByUserId(userId);
        List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();

        String role = member.getRole();
        authorities.add(new SimpleGrantedAuthority(role));


        return FuptoUserDetails.builder()
                .id(member.getId())
                .username(userId)
                .password(passwordEncoder.encode(member.getPassword()))
                .email(member.getEmail())
                .authorities(authorities)
                .build();
    }
}
