package com.example.demo123.controller;


import com.example.demo123.data.dao.PostDao;
import com.example.demo123.data.dto.Post;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

// org.springframework.web.bind.annotation
@RestController // that is itself annotated with @Controller and @ResponseBody
@RequestMapping("/api/v1/get-api")
public class GetController {

    public GetController(){
    }

    // 조건에 해당하는 글 전부를 조회
    // 인자가 둘중 하나 혹은 전부 제공되었을 시 dao를 통해 접근, 본 메서드에서 유효성 검사 수행
    // example: http://localhost:8085/api/v1/get-api/getposts?writer=me&title=TITLE
    @GetMapping("/lookUp")
    public ResponseEntity<HashMap> lookUpPosts(@RequestParam Map<String, String> params) {
        HttpHeaders httpHeaders= new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        Post post = new Post();
        HashMap<String, String> map = new HashMap<>();
        params.forEach((key, value) -> {
            switch (key) {
                case "writer" ->
                    post.setWriter(value);
                case "title" ->
                    post.setTitle(value);
                default ->
                    System.out.println("unKnown value");
            }
        });
        if (post.getWriter() == null && post.getTitle() == null) {
            throw new IllegalArgumentException("writer or title must be presented");
        }

        try {
            return new PostDao().lookUpPosts(post, httpHeaders);
        } catch (SQLException e) {
            map.put("SqlException", e.getMessage());
        } catch (Exception e) {
            map.put("OtherException", e.getMessage());
        }
        return new ResponseEntity<>(map, httpHeaders, 500);
    }

    // 특정 날짜의 글을 조회하는 메서드
    @GetMapping("/byDate/{date}")
    public ResponseEntity<HashMap> lookUpPostsByDate(@PathVariable String date) {
        HttpHeaders httpHeaders= new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        Post post = new Post();
        HashMap<String, String> map = new HashMap<>();

        // 주어진 날짜에 생성, 수정한 날짜를 전부 조회하나, 인자는 created_date 로 지정
        try {
            post.setCreated_date(date);
        } catch (IllegalArgumentException e) {
            map.put("IllegalArgumentException", e.getMessage());
            return new ResponseEntity<>(map, httpHeaders, 400);
        }

        try {
            return new PostDao().lookUpPostsByDate(post, httpHeaders);
        } catch (SQLException e) {
            map.put("SqlException", e.getMessage());
        } catch (Exception e) {
            map.put("OtherException", e.getMessage());
        }
        return new ResponseEntity<>(map, httpHeaders, 500);
    }
}
