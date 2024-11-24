package com.fupto.back.auth.util;

import com.fupto.back.auth.entity.FuptoUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

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
            System.out.println("JWT 검증 실패: " + e.getMessage());
            return false;
        }
    }

    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()//파서 빌드 생성
                .setSigningKey(secretKey) //서명키 설정 (토큰과 같은 값)
                .build()//파서 생성
                .parseClaimsJws(token) //토큰 검증 (형식,서명,만료)**jws로 변경?
                .getBody(); //토큰에서 페이로드를 추출함
    }

    public Long extractId(String token){
        return extractAllClaims(token).get("id",Long.class);
    }
    public String extractUsername(String token){
        return extractAllClaims(token).getSubject();
    }
    public String extractEmail(String token){
        return extractAllClaims(token).get("email",String.class);
    }
    public String extractnickname(String token){
        return extractAllClaims(token).get("nickname",String.class);
    }
    public List<String> extractRoles(String token){
        List<Map<String, String>> roles = extractAllClaims(token).get("roles", List.class);

        List<String> roleNames = new ArrayList<>();
        for(Map<String, String> role : roles) {
            System.out.println(role);
            roleNames.add(role.get("authority"));
            System.out.println(role.get("authority"));
        }
        return roleNames;
    }
    public String generateToken(FuptoUserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
        claims.put("id",userDetails.getId());
        claims.put("username",userDetails.getUsername());
        claims.put("password",userDetails.getPassword());
        claims.put("email",userDetails.getEmail());
        claims.put("roles",userDetails.getAuthorities());


      return Jwts.builder()
              .setClaims(claims)
              .setSubject(userDetails.getUsername())
              .setIssuedAt(new Date())
              .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
              .signWith(secretKey, SignatureAlgorithm.HS512)
              .compact();
    };


}
