package com.example.demo123.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public class CustomAuthenticationManager implements AuthenticationManager {
    private final UserDetailsService userDetailsService; // userDetailsServiceImpl

    public CustomAuthenticationManager(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 사용자가 제공한 인증 정보를 가져옵니다.
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // 사용자 이름을 통하여 조회
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("User not found");
        }

        // 사용자 정보(암호)를 확인하여 인증 수행
        if (!password.equals(userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        // 인증이 성공하면 UsernamePasswordAuthenticationToken 을 사용하여 Authentication 객체를 생성합니다.
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
