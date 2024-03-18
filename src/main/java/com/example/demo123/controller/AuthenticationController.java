package com.example.demo123.controller;


import com.example.demo123.component.jwt.*;
import com.example.demo123.data.dao.CustomUserDao;
import com.example.demo123.data.dao.RedisDao;
import com.example.demo123.data.dto.CustomUserDetails;
import com.example.demo123.data.dto.TokenWithSomeUserDetails;
import com.example.demo123.data.dto.controller.AuthenticationRequest;
import com.example.demo123.data.dto.controller.UserForm;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@PropertySource("classpath:application.properties")
@RestController
@RequestMapping("/api/v1/auth-api")
public class AuthenticationController {
    private final HttpHeaders httpHeaders;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final jwtUtil jwtUtil;
    private final RedisDao redisDao;
    private final CustomUserDao customUserDao;

    @Value("${jwt.validedPeriod}")
    private Integer validedPeriod;

    public AuthenticationController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, jwtUtil jwtUtil, HttpHeaders httpHeaders, RedisDao redisDao, CustomUserDao customUserDao) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.httpHeaders = httpHeaders;
        this.redisDao = redisDao;
        this.customUserDao = customUserDao;
    }

    // 토큰 발급, 무효화 양쪽 모두 인증 절차를 거쳐야 한다.

    @PostMapping("/issue")
    public ResponseEntity<TokenWithSomeUserDetails> issueJwtToken(@RequestBody AuthenticationRequest authenticationRequest) {
        // ---- 여기부터
        try {
            // 반환 객체를 참조하는 필드를 지정하지 않는다, 예외가 발생하지 않을 시 인증된 것으로 간주한다.
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            log.info("BadCredentialsException in AuthenticationController", e);
            return new ResponseEntity<>(null, httpHeaders, 400);
        } catch (AuthenticationException e) {
            log.info("AuthenticationException in AuthenticationController", e);
            return new ResponseEntity<>(null, httpHeaders, 500);
        } catch (Exception e) {
            log.info("Exception in AuthenticationController", e);
            return new ResponseEntity<>(null, httpHeaders, 500);
        }
        // ---- 여기까지 인증 관련 영역

        // id, pw 유효할 시 사용자 정보를 가져옴
        final CustomUserDetails customUserDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        // jwt, refresh 발급 (유효기간 100, 1000초)
        String Access = jwtUtil.generateJwt(null, customUserDetails, validedPeriod);
        String Refresh = jwtUtil.generateRefresh(null, validedPeriod * 10);
        TokenWithSomeUserDetails tokenWithSomeUserDetails = TokenWithSomeUserDetails.builder()
                .accessToken(Access)
                .refreshToken(Refresh)
                .email(customUserDetails.getEmail())
                .created_date(customUserDetails.getCreated_date())
                .last_modified(customUserDetails.getLast_modified()).build(); // 로그인 시 이메일 주소, 비밀번호 수정일자 등 사용자 세부 정보를 얻어올수 있음
        redisDao.deleteHashOperations("refresh", authenticationRequest.getUsername());
        // refresh Token 을 redis 에 저장
        redisDao.setHashOperations("refresh", authenticationRequest.getUsername(), Refresh);

        return new ResponseEntity<>(tokenWithSomeUserDetails, httpHeaders, 200);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> expireRefreshToken(@RequestHeader HttpHeaders headers) {
        String username;
        try {
            username = jwtUtil.extractUsername(headers.getFirst("Authorization")); // jwt 에서 사용자 이름 추출
            if (redisDao.getHashOperations("refresh", username).length() > 1) { // 리프래시 토큰이 조회될 시 이하 코드 실행
                redisDao.deleteHashOperations("refresh", username);
            }
        } catch (ExpiredJwtException e) {
            username = e.getClaims().getSubject();
            if (redisDao.getHashOperations("refresh", username).length() > 1) { // 리프래시 토큰이 조회될 시 이하 코드 실행
                redisDao.deleteHashOperations("refresh", username);
            } else { // 리프래시 토큰이 조회되지 않는 경우
                return new ResponseEntity<>(null, httpHeaders, 401);
            }
        }
        return new ResponseEntity<>(null, httpHeaders, 204);
    }


    // 로그인 상태에서 추가적인 본인 확인을 위해 비밀번호를 다시 확인
    @PostMapping("/verification")
    public ResponseEntity<TokenWithSomeUserDetails> re_verification(@RequestHeader HttpHeaders headers, @RequestBody UserForm userForm) { // dto 내부에 password 정의
        try {
            String username = jwtUtil.extractUsername(headers.getFirst("Authorization"));
            if (username != null) {
                userForm.setUsername(username); // 추후 다른 정보로도 조회할 여지를 남기고자 dto 에 값 할당하고 사용
                try {
                    String password = customUserDao.confirmPassword(userForm); // username 을 통해 password 조회
                    if (password != null) {
                        if (password.equals(userForm.getPassword())) { // 요청으로 들어온 암호, username 을 통해 조회한 암호의 일치 여부
                            Map<String, Object> claims = new HashMap<>();
                            claims.put("For", "re_verification"); // keyOfClaim, value
                            String TempToken_Mod_Pw = jwtUtil.generateTempToken(claims, username, 100); // 100초
                            TokenWithSomeUserDetails tokenWithUserDetails = TokenWithSomeUserDetails.builder()
                                    .accessToken(TempToken_Mod_Pw).build(); // accessToken 값에서 임시 토큰 확인 가능
                            return new ResponseEntity<>(tokenWithUserDetails, httpHeaders, 200);
                        } else {
                            return new ResponseEntity<>(null, httpHeaders, 401);
                        }
                    } else {
                        return new ResponseEntity<>(null, httpHeaders, 400);
                    }
                } catch (Exception e) {
                    log.warn("at AuthenticationController.re_verification", e);
                    return new ResponseEntity<>(null, httpHeaders, 500);
                }
            } else {
                return new ResponseEntity<>(null, httpHeaders, 401);
            }
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>(null, headers, 401);
        }
    }
}
