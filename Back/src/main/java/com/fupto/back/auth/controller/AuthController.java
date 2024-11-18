package com.fupto.back.auth.controller;

import com.fupto.back.auth.dto.AuthRequestDto;
import com.fupto.back.auth.entity.FuptoUserDetails;
import com.fupto.back.auth.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
    public String test(@AuthenticationPrincipal UserDetails details) {
        if (details != null) {
            return "username:" + details.getUsername() + ", roles:" + details.getAuthorities() + details;
        }else {return "유저를 못찾겠즤~";}
    }


    @PostMapping("sginin")
    public ResponseEntity<?> signin (@RequestBody AuthRequestDto requestDto){

        System.out.println(requestDto.getUsername());
        System.out.println(requestDto.getPassword());

        return ResponseEntity.ok("로그인 확인");

        }
    @PostMapping("signinn")
    public ResponseEntity<?> signinn(@RequestParam("username") String username,
                                 @RequestParam("password") String password){

        System.out.println(username);

        return ResponseEntity.ok("Signin successful");
    }
}
