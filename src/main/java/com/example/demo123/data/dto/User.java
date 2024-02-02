package com.example.demo123.data.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
// 유효성 검증은 setter 메서드 내에 별도로 구현
public class User {
    private String id;
    private String email;
    private String nick;
    private String password; // 비밀번호는 암호화 등 별도 조치를 취한 후 Dto를 통해 전송하는것이 바람직할 듯 하다
    private Provider provider;
    private String snsId;
}

enum Provider {
    local, kakao
}
