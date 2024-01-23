package com.example.demo123.controller;


import com.example.demo123.data.entity.PostEntity;
import com.example.demo123.data.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController // that is itself annotated with @Controller and @ResponseBody
@RequestMapping("/api/v1/get-api")
public class GetController {

    private final PostRepository postRepository;

    @Autowired // 필드 주입이 권장되지 않음, 생성자 인자에 자동와이어링
    public GetController(PostRepository postRepository){
        this.postRepository = postRepository;
    }

    // 어느 작성자(writer) 가 작성한 글 전부를 조회
    @GetMapping("/getPostsByWriter")
    public ResponseEntity<List<PostEntity>> readPosts(@RequestParam String writer) {
        if (writer == null){
            throw new IllegalArgumentException("writer is required"); // 적절한 응답을 하도록 처리하기
        }
        List<PostEntity> Posts = postRepository.findByWriter(writer);
        for (PostEntity post : Posts) {
            System.out.println("byWriter: " + post.getContent());
        }
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        // ResponseEntity(T body, MultiValueMap<String,String> headers, int rawStatus)
        return new ResponseEntity<>(Posts, headers, 200);
    }
}
