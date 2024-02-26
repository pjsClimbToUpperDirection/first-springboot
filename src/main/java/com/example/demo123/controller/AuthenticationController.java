package com.example.demo123.controller;

import com.example.demo123.data.dao.LoginStatusDao;
import com.example.demo123.data.dto.AuthenticationRequest;
import com.example.demo123.data.dto.Token;
import com.example.demo123.component.jwt.*;
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
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.AuthenticationManager;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

@Slf4j
@PropertySource("classpath:application.properties")
@RestController
@RequestMapping("/api/v1/auth-api")
public class AuthenticationController {
    HttpHeaders headers = new HttpHeaders();
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final jwtUtil jwtUtil;
    private final LoginStatusDao loginStatusDao;

    @Value("${jwt.validedPeriod}")
    private Integer validedPeriod;

    public AuthenticationController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, jwtUtil jwtUtil, LoginStatusDao loginStatusDao) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.loginStatusDao = loginStatusDao;
    }

    // 토큰 발급, 무효화 양쪽 모두 인증 절차를 거쳐야 한다.

    @PostMapping("/issue")
    public ResponseEntity<Token> issueJwtToken(@RequestBody AuthenticationRequest authenticationRequest) {
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        // ---- 여기부터
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
        // ---- 여기까지 인증 관련 영역

        // id, pw 유효할 시 사용자 정보를 가져옴
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        // jwt, refresh 발급 (유효기간 100, 1000초)
        String Access = jwtUtil.generateJwt(null, userDetails, validedPeriod);
        String Refresh = jwtUtil.generateRefresh(null, validedPeriod * 10);
        Token token = Token.builder()
                .accessToken(Access)
                .refreshToken(Refresh).build();
        try {
            loginStatusDao.activateLoginStatus(authenticationRequest.getUsername(), Refresh);
        } catch (SQLException e) {
            log.info("Exception on Inserting refreshToken in DB: " , e);
            return new ResponseEntity<>(null, headers, 500);
        }
        return new ResponseEntity<>(token, headers, 200);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> expireRefreshToken(@RequestHeader HttpHeaders httpHeaders) {
        try {
            loginStatusDao.destroyLoginStatus(httpHeaders.getFirst("refresh"));
        } catch (SQLException e) {
            log.info("Exception on deleting refreshToken in DB: " , e);
            return new ResponseEntity<>(null, headers, 500);
        }
        return new ResponseEntity<>(null, headers, 204);
    }
}
