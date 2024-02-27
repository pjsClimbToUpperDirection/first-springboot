package com.example.demo123.controller;

import com.example.demo123.data.dao.CustomUserDao;
import com.example.demo123.data.dto.CustomUserDetails;
import com.example.demo123.data.dto.controller.UserForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@PropertySource("classpath:application.properties")
@RestController
@RequestMapping("/api/v1/account-api")
public class AccountController {
    private final HttpHeaders httpHeaders;
    private final CustomUserDao customUserDao;

    public AccountController(HttpHeaders httpHeaders, CustomUserDao customUserDao) {
        this.httpHeaders = httpHeaders;
        this.customUserDao = customUserDao;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> CreateAccount(@RequestBody UserForm userForm) {
        try {
            if (userForm.getUsername() == null || userForm.getPassword() == null || userForm.getEmail() == null)
                throw new IllegalArgumentException("some argument that we require is not given to us");
            CustomUserDetails details = CustomUserDetails.builder()
                    .username(userForm.getUsername())
                    .password(userForm.getPassword())
                    .email(userForm.getEmail())
                    .build();
            customUserDao.createUser(details);
            return new ResponseEntity<>(null, httpHeaders, 200);
        } catch (Exception e) {
            log.warn("at UpdateController.UpdatePosts: ", e);
            return new ResponseEntity<>(null, httpHeaders, 400);
        }
    }

    // todo 비밀번호 수정, 계정 삭제 메서드 구현 필요(인증된 사용자가 해당 사용자인지 확인하는 로직 필요)
}
