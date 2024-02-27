package com.example.demo123.data.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.Set;

// 인증 시 사용되는 dto
// 불변 객체
@Builder
@Getter
public class CustomUserDetails implements UserDetails {
    private String id;
    private String username; // username 은 중복되는 값이 없도록 한다
    private String password;
    private String email;
    private boolean emailVerified;
    private boolean locked;
    private String role; // u, su

    @Override
    public Set<GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        // 인스턴스의 인자로 Authentication 객체에 부여된 권한을 나타내는 값을 적용, 반환 타입을 맞추고자 길이가 1인 형식적 set 인스턴스 생성
        authorities.add(new SimpleGrantedAuthority(this.role));
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
