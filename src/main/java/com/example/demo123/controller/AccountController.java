package com.example.demo123.controller;

import com.example.demo123.data.dao.CustomUserDao;
import com.example.demo123.data.dao.RedisDao;
import com.example.demo123.data.dto.CustomUserDetails;
import com.example.demo123.data.dto.controller.AuthNumberVerification;
import com.example.demo123.data.dto.controller.UserForm;
import com.example.demo123.service.AuthenticationNumberCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@PropertySource("classpath:application.properties")
@RestController
@RequestMapping("/api/v1/account-api")
public class AccountController {
    private final HttpHeaders httpHeaders;
    private final CustomUserDao customUserDao;
    private final RedisDao redisDao;
    private final AuthenticationNumberCreator authenticationNumberCreator;

    public AccountController(HttpHeaders httpHeaders, CustomUserDao customUserDao, RedisDao redisDao, AuthenticationNumberCreator authenticationNumberCreator) {
        this.httpHeaders = httpHeaders;
        this.customUserDao = customUserDao;
        this.redisDao = redisDao;
        this.authenticationNumberCreator = authenticationNumberCreator;
    }

    @PostMapping("/register/request")
    public ResponseEntity<Void> InitRequestForRegistration(@RequestBody UserForm userForm) {
        if (userForm.getUsername() != null && userForm.getPassword() != null && userForm.getEmail() != null) {
            // 사용자 정보를 redis 에 임시로 저장
            redisDao.setHashOperations(userForm.getUsername(), "username", userForm.getUsername());
            redisDao.setHashOperations(userForm.getUsername(), "password", userForm.getPassword());
            redisDao.setHashOperations(userForm.getUsername(), "email", userForm.getEmail());

            redisDao.setExpireTime(userForm.getUsername(), 90); // 90초 (인증번호는 70초)

            authenticationNumberCreator.AuthNumberCreation(userForm.getEmail(), "최초 가입 시 이메일 인증을 위한 인증번호", userForm.getUsername());
            return new ResponseEntity<>(null, httpHeaders, 204);
        } else {
            log.warn("some argument that we require is not given to us");
            return new ResponseEntity<>(null, httpHeaders, 400);
        }
    }

    @PostMapping("/register/verification")
    public ResponseEntity<Void> VerificationForRegistration(@RequestBody AuthNumberVerification authNumberVerification){
        String username = authenticationNumberCreator.AuthNumberVerifier(authNumberVerification);
        if (username != null) { // 인증번호 정확함
            try {
                customUserDao.createUser(CustomUserDetails.builder()
                        .username(redisDao.getHashOperations(username, "username"))
                        .password(redisDao.getHashOperations(username, "password"))
                        .email(redisDao.getHashOperations(username, "email"))
                        .emailVerified(true)
                        .build());
                return new ResponseEntity<>(null, httpHeaders, 200);
            } catch (Exception e) {
                log.warn("at AccountController.CreateAccount", e);
                return new ResponseEntity<>(null, httpHeaders, 500);
            }
        } else {
            return new ResponseEntity<>(null, httpHeaders, 401);
        }
    }
    // todo 비밀번호 수정, 계정 삭제 메서드 구현 필요(인증된 사용자가 해당 사용자인지 확인하는 로직 필요)
}
