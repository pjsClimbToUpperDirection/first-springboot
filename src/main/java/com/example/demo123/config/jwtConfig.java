package com.example.demo123.config;

import com.example.demo123.component.jwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.demo123.component.jwtExceptionHandler;

@Configuration
@EnableWebSecurity
public class jwtConfig {
    private final jwtExceptionHandler jwtExceptionHandler;
    private final jwtRequestFilter jwtRequestFilter;

    public jwtConfig(jwtExceptionHandler jwtExceptionHandler, jwtRequestFilter jwtRequestFilter) {
        this.jwtExceptionHandler = jwtExceptionHandler;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    // https://docs.spring.io/spring-security/reference/servlet/architecture.html#servlet-securityfilterchain
    // https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/web/builders/HttpSecurity.html
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        .requestMatchers("/api/v1/auth-api/issue").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionHandler, jwtRequestFilter.class)
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}
