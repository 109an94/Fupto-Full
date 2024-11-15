package com.fupto.back.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {
    private final Key secretKey;

    public JwtUtil(@Value("${fupto.jwt.secret}")String secret){
        byte[] keyBytes = Decoders.BASE64.decode(secret); //base64로 디코딩
        this.secretKey = Keys.hmacShaKeyFor(keyBytes); //거기에 hmac로 암호화
    }

    public boolean vaildateToken(String token){
        try{
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }

    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()//파서 빌드 생성
                .setSigningKey(secretKey) //서명키 설정 (토큰과 같은 값)
                .build()//파서 생성
                .parseClaimsJwt(token) //토큰 검증 (형식,서명,만료)**jws로 변경?
                .getBody(); //토큰에서 페이로드를 추출함
    }

    public String extractUsername(String token){
        return extractAllClaims(token).getSubject();
    }

    public String extractUserId(String token){
        return extractAllClaims(token).get("userId",String.class);
    }
    public String extractEmail(String token){
        return extractAllClaims(token).get("email",String.class);
    }
    public String extractnickname(String token){
        return extractAllClaims(token).get("nickname",String.class);
    }
    public String extractRoles(String token){
        return null;
    }

}
