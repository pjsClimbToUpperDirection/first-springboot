package com.example.demo123.data.dto;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    @Id
    private String id;
    private String email;
    @NotNull
    private String nick;
    private String password; // 비밀번호는 암호화 등 별도 조치를 취한 후 Dto를 통해 전송하는것이 바람직할 듯 하다
    @NotNull
    private Provider provider;
    private String snsId;
}

enum Provider {
    local, kakao
}
