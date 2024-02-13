package com.example.demo123.data.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

// 불변 객체
public class CustomUserDetails implements UserDetails {
    private String id;
    private String username;
    private String password;
    private String email;
    private boolean emailVerified;
    private boolean locked;
    private Collection<? extends GrantedAuthority> authorities;

    // 해당 사용자의 권한 목록
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // 'false' 일 시 만료됨
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 'true' 일 시 잠기지 않음
    @Override
    public boolean isAccountNonLocked() {
        return locked;
    }

    // 'false' 일 시 만료됨
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 이메일이 인증된 상태이며, 계정이 잠기지 않은 상태에서 활성화(true)
    @Override
    public boolean isEnabled() {
        return (emailVerified && !locked);
    }
}
