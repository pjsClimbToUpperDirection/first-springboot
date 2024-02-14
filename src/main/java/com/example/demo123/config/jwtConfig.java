package com.example.demo123.config;

import com.example.demo123.service.CustomAuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo123.service.jwt.jwtRequestFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class jwtConfig {

    // todo 추후 설정을 구성할 시 사용될 필드들
    private final UserDetailsService userDetailsService;
    private final jwtRequestFilter jwtRequestFilter;

    public jwtConfig(UserDetailsService userDetailsService, jwtRequestFilter jwtRequestFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests.anyRequest().authenticated())
                .httpBasic(withDefaults());
        return http.build();
    }

    // webSecurity 구성은 모든 요청 자원에 전역적으로 적용됨
    // https://stackoverflow.com/questions/56388865/spring-security-configuration-httpsecurity-vs-websecurity
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // ignoring() 메서드를 사용할 경우 spring security 의 전 기능을 사용할수 없다
        return (web) -> web.ignoring().requestMatchers("/authenticate");
    }

    // https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/authentication/AuthenticationManager.html
    @Bean
    public AuthenticationManager authenticationManager() {
        // todo 인증 성공시 완전히 구성된 Authentication 객체 반환
        return new CustomAuthenticationManager(userDetailsService);
    }
}
