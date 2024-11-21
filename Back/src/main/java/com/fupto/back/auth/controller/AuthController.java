package com.fupto.back.auth.controller;

import com.fupto.back.anonymous.member.dto.SignUpRequestDto;
import com.fupto.back.auth.dto.AuthRequestDto;
import com.fupto.back.auth.dto.AuthResponseDto;
import com.fupto.back.auth.entity.FuptoUserDetails;
import com.fupto.back.auth.exception.UserAlreadyExistsException;
import com.fupto.back.auth.service.FuptoUserDetailService;
import com.fupto.back.auth.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
public class AuthController {

    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;
    private FuptoUserDetailService fuptoUserDetailService;
    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil
    ,FuptoUserDetailService fuptoUserDetailService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.fuptoUserDetailService = fuptoUserDetailService;
    }

    @GetMapping("test")
    public String test(@AuthenticationPrincipal UserDetails details) {
        if (details != null) {
            return "username:" + details.getUsername() + ", roles:" + details.getAuthorities() + details;
        }else {return "유저를 못찾겠즤~";}
    }


//    @PostMapping("sginin")
//    public ResponseEntity<?> signin (@RequestBody AuthRequestDto requestDto){
//
//        System.out.println(requestDto.getUsername());
//        System.out.println(requestDto.getPassword());
//
//        return ResponseEntity.ok("로그인 확인");
//
//        }
    @PostMapping("signin")
    public ResponseEntity<?> signin (@RequestBody AuthRequestDto requestDto){
        System.out.println(requestDto);
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);
        System.out.println(authenticationToken);
        try{
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            FuptoUserDetails userDetails = (FuptoUserDetails) authentication.getPrincipal();

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtUtil.generateToken(userDetails);
            fuptoUserDetailService.updateLoginDate(username);
            System.out.println("인증완료");
            return ResponseEntity.ok(AuthResponseDto
                    .builder()
                    .userId(userDetails.getId())
                    .token(token)
                    .build());
        }
        catch (AuthenticationException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @PostMapping("signup")
    public ResponseEntity<?> signup (@RequestBody SignUpRequestDto requestDto){
        try {
            System.out.println("dto 매개변수 입력 :"+ requestDto);
            FuptoUserDetails userDetails = fuptoUserDetailService.regNewUser(requestDto);
            String token = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(AuthResponseDto
                    .builder()
                    .userId(userDetails.getId())
                    .token(token)
                    .build());
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus
                    .INTERNAL_SERVER_ERROR)
                    .body("Error during signup"+e.getMessage());
        }
    }
}
