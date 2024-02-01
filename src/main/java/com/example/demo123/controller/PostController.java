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

@RestController
@RequestMapping("/api/v1/post-api")
public class PostController {

    public PostController() {
    }

    // 한번에 하나의 포스트만 업로드 가능
    // 예시 -> http://localhost:8085/api/v1/post-api/uploadPost?email=eerI@gmail.com&content=Aop&writer=me
    @PostMapping("/uploadPost")
    public ResponseEntity<HashMap> UploadPost(@RequestParam Map<String, String> params){
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        Post post = new Post();

        Exception[] exceptions = new Exception[4];

        int len = 0;
        if(!params.containsKey("writer")) {
            exceptions[len] = new IllegalArgumentException("writer must be defined");
            len++;
        }
        if (!params.containsKey("content")) {
            exceptions[len] = new IllegalArgumentException("content must be defined");
            len++;
        }
        if (!params.containsKey("email")) {
            exceptions[len] = new IllegalArgumentException("email address must be defined");
            len++;
        }
        if (!params.containsKey("title")) {
            exceptions[len] = new IllegalArgumentException("title must be defined");
        }

        if (exceptions[0] != null) { // 하나 이상의 예외가 존재할 시(배열의 첫 값이 null 이 아닐 시)
            HashMap<String, String> map = new HashMap<>();
            for (Exception e : exceptions) {
                if (e != null) {
                    switch (e.getMessage()) {
                        case "writer must be defined" -> {
                            map.put("writer", e.toString());
                        }
                        case "content must be defined" -> {
                            map.put("content", e.toString());
                        }
                        case "email address must be defined" -> {
                            map.put("email", e.toString());
                        }
                        case "title must be defined" -> {
                            map.put("title", e.toString());
                        }
                        default -> {
                        }
                    }
                }
            }
            return new ResponseEntity<>(map, headers, 400);
        } else {
            // 구문이 재사용될시 분리하기
            // key=value 인자의 순서를 알수 없기에 case 문 반복
            HashMap<String, String> map = new HashMap<>();

            params.forEach((key, value) -> {
                switch (key) {
                    case "writer" -> {
                        post.setWriter(value);
                    }
                    case "title" -> {
                        post.setTitle(value);
                    }
                    case "content" -> {
                        post.setContent(value);
                    }
                    case "email" -> {
                        post.setEmail(value);
                    }
                    default -> {
                        System.out.println("unKnown value");
                    }
                }
            });
            try {
                return new PostDao().InsertPost(post, headers);
            } catch (SQLException e) {
                map.put("SqlException", e.getMessage());
            } catch (Exception e) {
                map.put("OtherException", e.getMessage());;
            }
            return new ResponseEntity<>(map, headers, 500);
        }
    }
}
