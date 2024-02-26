package com.example.demo123.data.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


// 컨트롤러 영역에서 사용자 요청을 받을때 사용되지만 변경 가능성이 낮은 dto 로 판단되어 의존적 상태 유지
@Getter
@Setter
@NoArgsConstructor
public class Post {
    private Integer post_id;
    private String writer;
    private String title;
    private String content;
    private String email; // 이메일 형식을 준수해야 함
    private String created_date;
    private String updated_date;
}
