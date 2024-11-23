package com.fupto.back.auth.filter;

import com.fupto.back.auth.entity.FuptoUserDetails;
import com.fupto.back.auth.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getServletPath().startsWith("/auth/") ||
                request.getServletPath().startsWith("/products") ||
                request.getServletPath().startsWith("/brands") ||
                request.getServletPath().startsWith("/shoppingmalls") ||
                request.getServletPath().matches(".*/products/.*/image/.*") ||
                request.getServletPath().matches("/products/.*/image/.*") ||
                request.getServletPath().startsWith("/uploads/")) {
            filterChain.doFilter(request, response);
        }
        else {
            String authHeader = request.getHeader("Authorization");

            System.out.println("authHeader : "+ authHeader);
            System.out.println("request : "+ request); //확인됨

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    System.out.println("token : "+ token);

                    if (jwtUtil.vaildateToken(token)) {
                        String username = jwtUtil.extractUsername(token);
                        List<String> roles = jwtUtil.extractRoles(token);
                        System.out.println("username : "+username);
                        System.out.println("roles : "+roles);

                        if (username != null && !username.isEmpty()) {
                            List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
                            authorities.add(new SimpleGrantedAuthority(username));


                            for (String role : roles) {
                                authorities.add(new SimpleGrantedAuthority(role));
                            }
                            UserDetails userDetails = FuptoUserDetails.builder()
                                    .username(username)
                                    .authorities(authorities).build();

                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        }
                    }
                }
                else {
                    System.out.println("jwt 토큰 검증 실패");
    //                jwtUtil.generateToken(request);
                    return ;
                }
    //            else {filterChain.doFilter(request, response);
    //                return;}
                filterChain.doFilter(request, response);
        }
    }
}
