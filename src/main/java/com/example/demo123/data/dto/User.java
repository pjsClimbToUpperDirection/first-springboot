package com.example.demo123.data.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@NoArgsConstructor
// 유효성 검증은 setter 메서드 내에 별도로 구현
public class User {
    @NotNull
    private String user_name; // id 역할, 고윳값
    @NotNull
    private String email;
    private String password; // 비밀번호는 암호화 등 별도 조치 필요, 사용자 최초 생성시 별도로 null 검증
}
