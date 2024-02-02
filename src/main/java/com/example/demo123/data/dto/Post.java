package com.example.demo123.data.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;


// http://localhost:8085/api/v1/post-api/uploadPost?writer=me&email=something@gmail.com&...
// dto is on duty for validation
@Getter
@NoArgsConstructor
public class Post {

    private String id;
    private String writer;
    private String title;
    private String content;
    private String email; // 이메일 형식을 준수해야 함
    private String created_date;
    private String updated_date;

    // 별도 setter 메서드 내부에 자체적인 검증 로직을 구현
    public void setEmail(String email) {
        // 정규 표현식 (Regular expression) 사용됨
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) { // @ 앞에 대소문자 알파벳, 0~9까지의 숫자, 언더바,점,하이픈 허용됨, 뒤로는 하나 이상의 어떠한 문자가 존재해야 함
            throw new IllegalArgumentException("this is not valid for used to email address");// 예외 처리 로직 별도로 구현할 것
        }
        this.email = email;
    }

    public void setWriter(String writer) {
        if (writer == null) {
            throw new IllegalArgumentException("writer should not be null");// 예외 처리 로직 별도로 구현할 것
        }
        this.writer = writer;
    }

    public void setTitle(String title) {
        if (title == null) {
            throw new IllegalArgumentException("title should not be null");// 예외 처리 로직 별도로 구현할 것
        }
        this.title = title;
    }

    public void setContent(String content) {
        if (content == null) {
            throw new IllegalArgumentException("content should not be null");// 예외 처리 로직 별도로 구현할 것
        }
        this.content = content;
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
