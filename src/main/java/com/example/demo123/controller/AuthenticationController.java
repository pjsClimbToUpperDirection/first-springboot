package com.example.demo123.controller;

import com.example.demo123.data.dto.AuthenticationRequest;
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

import com.example.demo123.service.jwt.jwtUtil;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/auth-api")
public class AuthenticationController {
    HttpHeaders headers= new HttpHeaders();
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final jwtUtil jwtUtil;

    public AuthenticationController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, jwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<HashMap<String, String>> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        HashMap<String, String> map = new HashMap<>();

        try {
            // authenticationManager.authenticate: 전달된 Authentication 객체를 인증하려 시도, 성공할 시 완전히 구성된 Authentication 반환
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            map.put("BadCredentialsException", e.getMessage());
            return new ResponseEntity<>(map, headers, 400);
        } catch (AuthenticationException e) {
            map.put("AuthenticationException", e.getMessage());
            return new ResponseEntity<>(map, headers, 500);
        } catch (Exception e) {
            map.put("Exception", e.getMessage());
            return new ResponseEntity<>(map, headers, 500);
        }

        // id, pw 유효할 시 사용자 정보를 가져옴
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        map.put("jwt", jwtUtil.generateToken(null, userDetails));

        return new ResponseEntity<>(map, headers, 200);
    }
}
