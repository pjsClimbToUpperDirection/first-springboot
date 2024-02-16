package com.example.demo123.config;

import com.example.demo123.service.CustomAuthenticationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo123.service.jwt.jwtRequestFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class jwtConfig {
    private final jwtRequestFilter jwtRequestFilter;

    public jwtConfig(jwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    // https://docs.spring.io/spring-security/reference/servlet/architecture.html#servlet-securityfilterchain
    // https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/web/builders/HttpSecurity.html
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class).authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests.anyRequest().authenticated())
                .httpBasic(withDefaults()).build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // ignoring() 메서드를 사용할 경우 spring security 의 전 기능을 사용할수 없다
        return (web) -> web.ignoring().requestMatchers("/api/v1/auth-api/issue");
    }
}
