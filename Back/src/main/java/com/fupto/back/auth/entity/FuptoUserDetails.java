package com.fupto.back.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FuptoUserDetails implements UserDetails {

    private Long id;
    private String username; //userId에 해당하는 격
    private String password;
    private String email;

    private Collection<? extends GrantedAuthority> authorities;


}
