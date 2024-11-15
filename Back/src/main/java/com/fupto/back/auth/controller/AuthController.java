package com.fupto.back.auth.controller;

import com.fupto.back.auth.entity.FuptoUserDetails;
import com.fupto.back.auth.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthController {
//    private AuthenticationManager authenticationManager;
//    private JwtUtil jwtUtil;
//    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
//        this.authenticationManager = authenticationManager;
//        this.jwtUtil = jwtUtil;
//    }
    @GetMapping("test")
    public String test(@AuthenticationPrincipal FuptoUserDetails details) {
        if (details != null) {
            return "username:" + details.getUsername() + ", roles:" + details.getAuthorities();
        }else {return "유저를 못찾겠즤~";}
    }
}
