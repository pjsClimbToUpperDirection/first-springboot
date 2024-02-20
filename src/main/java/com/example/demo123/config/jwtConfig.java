package com.example.demo123.config;

import com.example.demo123.component.jwtRequestFilter;
import com.example.demo123.component.jwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class jwtConfig {
    private final UserDetailsService userDetailsService;
    private final jwtUtil jwtUtil;

    public jwtConfig(UserDetailsService userDetailsService, com.example.demo123.component.jwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    // https://docs.spring.io/spring-security/reference/servlet/architecture.html#servlet-securityfilterchain
    // https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/web/builders/HttpSecurity.html
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        BasicAuthenticationEntryPoint authenticationEntryPoint = new BasicAuthenticationEntryPoint();
        authenticationEntryPoint.setRealmName("My Realm");

        return httpSecurity
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        .requestMatchers("/api/v1/auth-api/issue").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                //.exceptionHandling((exceptionHandling) -> exceptionHandling
                //        .accessDeniedHandler()
                //)
                .addFilterBefore(new jwtRequestFilter(userDetailsService, jwtUtil, authenticationEntryPoint), UsernamePasswordAuthenticationFilter.class)
                // 인증이 실패할 시 예외처리
                .httpBasic((httpSecurityHttpBasicConfigurer -> httpSecurityHttpBasicConfigurer.authenticationEntryPoint(authenticationEntryPoint)))
                .build();
    }
}
