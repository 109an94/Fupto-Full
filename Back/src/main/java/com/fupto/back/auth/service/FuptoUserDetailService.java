package com.fupto.back.auth.service;

import com.fupto.back.auth.entity.FuptoUserDetails;
import com.fupto.back.entity.Member;
import com.fupto.back.repository.MemberRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FuptoUserDetailService implements UserDetailsService {

private MemberRepository memberRepository;
public FuptoUserDetailService(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
}

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
//        if (username.equals("admin")){}
        Member member = memberRepository.findByUserId(userId);
        List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        return FuptoUserDetails.builder()
                .id(member.getId())
                .username(userId)
                .password(member.getPassword())
                .email(member.getEmail())
                .authorities(authorities)
                .build();
    }
}
