package com.example.demo123.controller;

import com.example.demo123.component.jwt.jwtUtil;
import com.example.demo123.data.dao.CustomUserDao;
import com.example.demo123.data.dao.RedisDao;
import com.example.demo123.data.dto.CustomUserDetails;
import com.example.demo123.data.dto.Token;
import com.example.demo123.data.dto.controller.AuthNumberVerification;
import com.example.demo123.data.dto.controller.UserForm;
import com.example.demo123.service.AuthenticationNumberCreator;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@PropertySource("classpath:application.properties")
@RestController
@RequestMapping("/api/v1/account-api")
public class AccountController {
    private final HttpHeaders httpHeaders;
    private final CustomUserDao customUserDao;
    private final RedisDao redisDao;
    private final AuthenticationNumberCreator authenticationNumberCreator;
    private final jwtUtil jwtUtil;

    public AccountController(HttpHeaders httpHeaders, CustomUserDao customUserDao, RedisDao redisDao, AuthenticationNumberCreator authenticationNumberCreator, jwtUtil jwtUtil) {
        this.httpHeaders = httpHeaders;
        this.customUserDao = customUserDao;
        this.redisDao = redisDao;
        this.authenticationNumberCreator = authenticationNumberCreator;
        this.jwtUtil = jwtUtil;
    }




    @PostMapping("/register/request")
    public ResponseEntity<Void> InitRequestForRegistration(@RequestBody UserForm userForm) {
        if (userForm.getUsername() != null && userForm.getPassword() != null && userForm.getEmail() != null) {  // 사용자 정보를 redis 에 임시로 저장
            redisDao.setHashOperations(userForm.getUsername() + "_for_register", "username", userForm.getUsername());
            redisDao.setHashOperations(userForm.getUsername() + "_for_register", "password", userForm.getPassword());
            redisDao.setHashOperations(userForm.getUsername() + "_for_register", "email", userForm.getEmail());

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
                        .username(redisDao.getHashOperations(username + "_for_register", "username"))
                        .password(redisDao.getHashOperations(username + "_for_register", "password"))
                        .email(redisDao.getHashOperations(username + "_for_register", "email"))
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




    // 로그인 상태에서 추가적인 본인 확인을 위해 비밀번호를 다시 확인
    @PostMapping("/modification/email/request")
    public ResponseEntity<Void> confirmPasswordForModificationOfEmailAddress(@RequestHeader HttpHeaders headers, @RequestBody UserForm userForm) { // dto 내에 email, password 정의
        try {
            String username = jwtUtil.extractUsername(headers.getFirst("Authorization"));
            if (username != null) {
                userForm.setUsername(username);
                try {
                    String password = customUserDao.confirmPassword(userForm); // username 을 통해 password 조회
                    if (password != null) {
                        if (password.equals(userForm.getPassword())) { // 요청으로 들어온 암호, username 을 통해 조회한 암호의 일치 여부
                            redisDao.setValues(username + "_for_modification_of_emailAddress", userForm.getEmail(), 100); // 100초 (인증번호는 70초)
                            authenticationNumberCreator.AuthNumberCreation(userForm.getEmail(), "이메일 주소 변경시 주소의 유효함을 검증하기 위한 인증번호", username); // 인증번호 전송
                            return new ResponseEntity<>(null, headers, 204);
                        } else {
                            return new ResponseEntity<>(null, httpHeaders, 401);
                        }
                    } else {
                        return new ResponseEntity<>(null, httpHeaders, 400);
                    }
                } catch (Exception e) {
                    log.warn("at AccountController.confirmPasswordForModificationOfMailAddress", e);
                    return new ResponseEntity<>(null, httpHeaders, 500);
                }
            } else {
                return new ResponseEntity<>(null, httpHeaders, 401);
            }
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>(null, headers, 401);
        }
    }
    @PatchMapping("/modification/email/verification")
    public ResponseEntity<Void> modifyEmailAddress(@RequestBody AuthNumberVerification authNumberVerification) {
        String username = authenticationNumberCreator.AuthNumberVerifier(authNumberVerification);
        if (username != null) { // 인증번호가 정확할 시
            try {
                customUserDao.ModifyEmailAddress(redisDao.getValues(username + "_for_modification_of_emailAddress"), username);
                return new ResponseEntity<>(null, httpHeaders, 200);
            } catch (Exception e) {
                log.warn("at AccountController.modifyEmailAddress", e);
                return new ResponseEntity<>(null, httpHeaders, 500);
            }
        } else {
            return new ResponseEntity<>(null, httpHeaders, 401);
        }
    }




    // 로그인 상태에서 추가적인 본인 확인을 위해 비밀번호를 다시 확인
    @PostMapping("/modification/password/request")
    public ResponseEntity<Token> confirmPreviousPasswordForModificationOfPassword(@RequestHeader HttpHeaders headers, @RequestBody UserForm userForm) { // dto 내에 (기존)password 정의
        try {
            String username = jwtUtil.extractUsername(headers.getFirst("Authorization"));
            if (username != null) {
                userForm.setUsername(username); // 추후 다른 정보로도 조회할 여지를 남기고자 dto 에 값 할당하고 사용
                try {
                    String password = customUserDao.confirmPassword(userForm); // username 을 통해 password 조회
                    if (password != null) {
                        if (password.equals(userForm.getPassword())) { // 요청으로 들어온 암호, username 을 통해 조회한 암호의 일치 여부
                            Map<String, Object> claims = new HashMap<>();
                            claims.put("For_modification_of", "password"); // keyOfClaim, value
                            String TempToken_Mod_Pw = jwtUtil.generateTempToken(claims, username, 100);
                            Token token = Token.builder()
                                    .accessToken(TempToken_Mod_Pw).build(); // accessToken 값에서 임시 토큰 확인 가능
                            return new ResponseEntity<>(token, httpHeaders, 200);
                        } else {
                            return new ResponseEntity<>(null, httpHeaders, 401);
                        }
                    } else {
                        return new ResponseEntity<>(null, httpHeaders, 400);
                    }
                } catch (Exception e) {
                    log.warn("at AccountController.confirmPreviousPasswordForModificationOfPassword", e);
                    return new ResponseEntity<>(null, httpHeaders, 500);
                }
            } else {
                return new ResponseEntity<>(null, httpHeaders, 401);
            }
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>(null, headers, 401);
        }
    }
    @PatchMapping("/modification/password/verification")
    public ResponseEntity<Void> modifyPassword (@RequestHeader HttpHeaders headers, @RequestBody UserForm userForm) { // dto 내에 (변경)password 정의
        try {
            String TempToken = headers.getFirst("Authorization_Modification_PW");
            String username = jwtUtil.extractUsername(TempToken);
            String purposeSign = jwtUtil.extractSpecificClaim(TempToken, "For_modification_of");
            if (username != null && Objects.equals(purposeSign, "password")) {
                userForm.setUsername(username); // 추후 다른 정보로도 조회할 여지를 남기고자 dto 에 값 할당하고 사용
                try {
                    customUserDao.ModifyPassword(userForm);
                    return new ResponseEntity<>(null, httpHeaders, 200);
                } catch (Exception e) {
                    log.warn("at AccountController.modifyPassword", e);
                    return new ResponseEntity<>(null, httpHeaders, 500);
                }
            } else {
                return new ResponseEntity<>(null, httpHeaders, 401);
            }
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>(null, headers, 401);
        }
    }



    // 로그인 상태에서 추가적인 본인 확인을 위해 비밀번호를 다시 확인
    @PostMapping("/deletion/request")
    public ResponseEntity<Token> ConfirmPasswordForDeletionOfAccount(@RequestHeader HttpHeaders headers, @RequestBody UserForm userForm) { // dto 내에 (기존) password 정의
        try {
            String username = jwtUtil.extractUsername(headers.getFirst("Authorization"));
            if (username != null) {
                userForm.setUsername(username); // 추후 다른 정보로도 조회할 여지를 남기고자 dto 에 값 할당하고 사용
                try {
                    String password = customUserDao.confirmPassword(userForm); // username 을 통해 password 조회
                    if (password != null) {
                        if (password.equals(userForm.getPassword())) { // 요청으로 들어온 암호, username 을 통해 조회한 암호의 일치 여부
                            Map<String, Object> claims = new HashMap<>();
                            claims.put("For_deletion_of", "account");
                            String TempToken_Del_Ac = jwtUtil.generateTempToken(claims, username, 100);
                            Token token = Token.builder()
                                    .accessToken(TempToken_Del_Ac).build(); // accessToken 값에서 임시 토큰 확인 가능
                            return new ResponseEntity<>(token, httpHeaders, 200);
                        } else {
                            return new ResponseEntity<>(null, httpHeaders, 401);
                        }
                    } else {
                        return new ResponseEntity<>(null, httpHeaders, 400);
                    }
                } catch (Exception e) {
                    log.warn("at AccountController.ConfirmPasswordForDeletionOfAccount", e);
                    return new ResponseEntity<>(null, httpHeaders, 500);
                }
            } else {
                return new ResponseEntity<>(null, httpHeaders, 401);
            }
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>(null, headers, 401);
        }
    }
    @DeleteMapping("/deletion/verification")
    public ResponseEntity<Void> deleteAccount(@RequestHeader HttpHeaders headers) {
        try {
            String TempToken = headers.getFirst("Authorization_Deletion_AC");
            String username = jwtUtil.extractUsername(TempToken);
            String purposeSign = jwtUtil.extractSpecificClaim(TempToken, "For_deletion_of");
            if (username != null && Objects.equals(purposeSign, "account")) {
                try {
                    customUserDao.DeleteUser(username);
                    return new ResponseEntity<>(null, httpHeaders, 200);
                } catch (Exception e) {
                    log.warn("at AccountController.deleteAccount", e);
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
