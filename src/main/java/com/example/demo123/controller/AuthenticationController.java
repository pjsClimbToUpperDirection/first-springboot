package com.example.demo123.controller;

import com.example.demo123.data.dto.AuthenticationRequest;
import com.example.demo123.data.dto.Token;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.AuthenticationManager;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import com.example.demo123.component.jwtUtil;

@Slf4j
@PropertySource("classpath:application.properties")
@RestController
@RequestMapping("/api/v1/auth-api")
public class AuthenticationController {
    HttpHeaders headers= new HttpHeaders();
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final jwtUtil jwtUtil;

    @Value("${jwt.validedPeriod}")
    private Integer validedPeriod;

    public AuthenticationController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, jwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/issue")
    public ResponseEntity<Token> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        HashMap<String, String> map = new HashMap<>();
        try {
            // 반환 객체를 참조하는 필드를 지정하지 않는다, 예외가 발생하지 않을 시 인증된 것으로 간주한다.
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            log.info("BadCredentialsException in AuthenticationController", e);
            return new ResponseEntity<>(null, headers, 400);
        } catch (AuthenticationException e) {
            log.info("AuthenticationException in AuthenticationController", e);
            return new ResponseEntity<>(null, headers, 500);
        } catch (Exception e) {
            log.info("Exception in AuthenticationController", e);
            return new ResponseEntity<>(null, headers, 500);
        }

        // id, pw 유효할 시 사용자 정보를 가져옴
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        // jwt, refresh 발급 (유효기간 100, 1000초)
        // jwtUtil.generateJwt(null, userDetails, 100), jwtUtil.generateRefresh(null, userDetails, 1000)
        Token token = Token.builder()
                .accessToken(jwtUtil.generateJwt(null, userDetails, validedPeriod))
                .refreshToken(jwtUtil.generateRefresh(null, userDetails, validedPeriod * 10)).build();

        return new ResponseEntity<>(token, headers, 200);
    }
}
