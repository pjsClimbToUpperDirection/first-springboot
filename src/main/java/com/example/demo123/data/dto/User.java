package com.example.demo123.data.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class User {
    // 해당 dto 는 데이터 전달만을 수행하도록 제작하였습니다.
    // 유효성 검증은 컨트롤러에 위임합니다.
    private String user_name; // id 역할, 고윳값
    private String email;
    private String password; // 비밀번호는 암호화 등 별도 조치 필요, 사용자 최초 생성시 별도로 null 검증
}
