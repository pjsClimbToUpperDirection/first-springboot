package com.example.demo123.data.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class Post {
    // 각각의 컨트롤러에서 데이터를 전송하는 경우에 따라 값이 할당되는 필드가 제각각이므로 null 검증이 생략됨
    private Integer post_id;
    private String writer;
    private String title;
    private String content;
    private String email; // 이메일 형식을 준수해야 함
    private String created_date;
    private String updated_date;

    // 별도 setter 메서드 내부에 자체적인 검증 로직을 구현
    // dto 내의 검증은 값의 존재 여부가 아닌 값의 형식이 요구사항을 충족하는지 여부만을 확인
    public void setEmail(String email) {
        // 정규 표현식 (Regular expression) 사용됨
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) { // @ 앞에 대소문자 알파벳, 0~9까지의 숫자, 언더바,점,하이픈 허용됨, 뒤로는 하나 이상의 어떠한 문자가 존재해야 함
            throw new IllegalArgumentException("this is not valid for used to email address");
        }
        this.email = email;
    }

    public void setCreated_date(String created_date) {
        if (!created_date.matches("^[2-3][0-9]-[0-1][0-9]-[0-3][0-9]")) {
            throw new IllegalArgumentException("date type should be observed specific format, for example: 24-07-22");
        }
        this.created_date = created_date;
    }

    public void setUpdated_date(String updated_date) {
        if (!updated_date.matches("^[2-3][0-9]-[0-1][0-9]-[0-3][0-9]")) {
            throw new IllegalArgumentException("date type should be observed specific format, for example: 24-07-22");
        }
        this.updated_date = updated_date;
    }
}
