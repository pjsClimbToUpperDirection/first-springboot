package com.example.demo123.service;

import com.example.demo123.data.dto.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // todo CustomUserDao 접근 로직 작성
        CustomUserDetails customUserDetails = new CustomUserDetails();
        // todo 해당 영역에 dto 값 설정
        return customUserDetails;
    }
}
