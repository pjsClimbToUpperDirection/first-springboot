package com.example.demo123.controller;

import com.example.demo123.component.jwt.jwtUtil;
import com.example.demo123.data.dao.CustomUserDao;
import com.example.demo123.data.dao.RedisDao;
import com.example.demo123.data.dto.CustomUserDetails;
import com.example.demo123.data.dto.controller.AuthNumberVerification;
import com.example.demo123.data.dto.controller.UserForm;
import com.example.demo123.service.AuthenticationNumberCreator;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.regex.Pattern;

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
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (userForm.getUsername() != null && userForm.getPassword() != null && userForm.getEmail() != null && Pattern.compile(emailPattern).matcher(userForm.getEmail()).matches()) {
            // 해당 사용자 이름으로 이미 계정 생성을 시도하는 중일 시 같은 사용자 이름의 요청을 막아 계정 정보 오염 방지
            if (!Objects.equals(redisDao.getHashOperations(userForm.getUsername() + "_for_register", "username"), userForm.getUsername())) {
                // 사용자 정보를 redis 에 임시로 저장
                redisDao.setHashOperations(userForm.getUsername() + "_for_register", "username", userForm.getUsername());
                redisDao.setHashOperations(userForm.getUsername() + "_for_register", "password", userForm.getPassword());
                redisDao.setHashOperations(userForm.getUsername() + "_for_register", "email", userForm.getEmail());

                redisDao.setExpireTime(userForm.getUsername() + "_for_register", 90); // 90초 (인증번호는 70초)

                authenticationNumberCreator.AuthNumberCreation(userForm.getEmail(), "최초 가입 시 이메일 인증을 위한 인증번호", userForm.getUsername());
                return new ResponseEntity<>(null, httpHeaders, 204);
            } else {
                return new ResponseEntity<>(null, httpHeaders, 409); // 409 Conflict (같은 사용자 이름으로 타인이 계정 생성 시도 중)
            }
        } else {
            log.warn("some argument that we require is not given to us");
            return new ResponseEntity<>(null, httpHeaders, 400);
        }
    }
    @PostMapping("/register/verification")
    public ResponseEntity<Void> VerificationForRegistration(@RequestBody AuthNumberVerification authNumberVerification){
        String username = authenticationNumberCreator.AuthNumberVerifier(authNumberVerification);
        if (username != null) { // 인증번호가 정확할 시
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
    public ResponseEntity<Void> confirmPasswordForModificationOfEmailAddress(@RequestHeader HttpHeaders headers, @RequestBody UserForm userForm) { // dto 내에 email 정의
        try {
            String TempToken = headers.getFirst("Authorization_Modification_Email");
            String usernameFromTemp = jwtUtil.extractUsername(TempToken);
            String username = jwtUtil.extractUsername(headers.getFirst("Authorization"));
            String purposeSign = jwtUtil.extractSpecificClaim(TempToken, "For");
            if (Objects.equals(usernameFromTemp, username) && Objects.equals(purposeSign, "re_verification")) {
                authenticationNumberCreator.AuthNumberCreation(userForm.getEmail(), "이메일 주소 변경시 주소의 유효함을 검증하기 위한 인증번호", username); // 인증번호 전송
                redisDao.setValues(username + "_for_modification_of_emailAddress", userForm.getEmail(), 90); // 90초 (인증번호는 70초)
                return new ResponseEntity<>(null, headers, 204);
            } else {
                return new ResponseEntity<>(null, httpHeaders, 401);
            }
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>(null, headers, 401);
        }
    }
    // 인증번호 확인을 위한 로직
    @PatchMapping("/modification/email/verification")
    public ResponseEntity<Void> modifyEmailAddress(@RequestBody AuthNumberVerification authNumberVerification) {
        String username = authenticationNumberCreator.AuthNumberVerifier(authNumberVerification);
        if (username != null) { // 인증번호가 정확할 시
            String Address = redisDao.getValues(username + "_for_modification_of_emailAddress");
            if (Address != null) { // 임시 저장된 이메일 주소의 유효기간 만료 등의 이유로 자료를 찾을 수 없는 경우에 대응
                try {
                    customUserDao.ModifyEmailAddress(redisDao.getValues(username + "_for_modification_of_emailAddress"), username);
                    return new ResponseEntity<>(null, httpHeaders, 200);
                } catch (Exception e) {
                    log.warn("at AccountController.modifyEmailAddress", e);
                    return new ResponseEntity<>(null, httpHeaders, 500);
                }
            } else {
                return new ResponseEntity<>(null, httpHeaders, 404); // 해당 인증번호로 이메일 주소를 조회할 수 없을 시
            }
        } else {
            return new ResponseEntity<>(null, httpHeaders, 401);
        }
    }




    @PatchMapping("/modification/password/request")
    public ResponseEntity<Void> modifyPassword (@RequestHeader HttpHeaders headers, @RequestBody UserForm userForm) { // dto 내에 (변경)password 정의
        try {
            String TempToken = headers.getFirst("Authorization_Modification_PW");
            String usernameFromTemp = jwtUtil.extractUsername(TempToken);
            String username = jwtUtil.extractUsername(headers.getFirst("Authorization"));
            String purposeSign = jwtUtil.extractSpecificClaim(TempToken, "For");
            if (Objects.equals(usernameFromTemp, username) && Objects.equals(purposeSign, "re_verification")) {
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




    @DeleteMapping("/deletion/request")
    public ResponseEntity<Void> deleteAccount(@RequestHeader HttpHeaders headers) {
        try {
            String TempToken = headers.getFirst("Authorization_Deletion_AC");
            String usernameFromTemp = jwtUtil.extractUsername(TempToken);
            String username = jwtUtil.extractUsername(headers.getFirst("Authorization"));
            String purposeSign = jwtUtil.extractSpecificClaim(TempToken, "For");
            if (Objects.equals(usernameFromTemp, username) && Objects.equals(purposeSign, "re_verification")) {
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
