package com.example.demo123.data.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Post {
    // 해당 dto 는 데이터 전달만을 수행하도록 제작하였습니다.
    // 유효성 검증은 컨트롤러에 위임합니다.
    private Integer post_id;
    private String writer;
    private String title;
    private String content;
    private String email; // 이메일 형식을 준수해야 함
    private String created_date;
    private String updated_date;
}
