package com.example.demo123.controller;

import com.example.demo123.data.dao.PostDao;
import com.example.demo123.data.dao.UserDao;
import com.example.demo123.data.dto.Post;
import com.example.demo123.data.dto.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/post-api")
public class PostController {

    public PostController() {}

    // 한번에 하나의 포스트만 업로드 가능
    // 예시 -> http://localhost:8085/api/v1/post-api/uploadPost?email=eerI@gmail.com&content=Aop&writer=me
    @PostMapping("/uploadPost")
    public ResponseEntity<HashMap<String, String>> UploadPost(@RequestBody Post post){
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        HashMap<String, String> mapForException = new HashMap<>();
        if(post.getWriter() == null) {
            mapForException.put("IllegalArgumentException-writer","writer must be defined");
        }
        if (post.getContent() == null) {
            mapForException.put("IllegalArgumentException-content","content must be defined");
        }
        if (post.getEmail() == null) {
            mapForException.put("IllegalArgumentException-email","email must be defined");
        }
        if (post.getTitle() == null) {
            mapForException.put("IllegalArgumentException-title","title must be defined");
        }

        if (!mapForException.isEmpty())// 하나 이상의 예외가 존재할 시(배열의 첫 값이 null 이 아닐 시)
            return new ResponseEntity<>(mapForException, headers, 400);

        try {
            return new PostDao().InsertPost(post, headers);
        } catch (SQLException e) {
            mapForException.put("SqlException", e.getMessage());
        } catch (Exception e) {
            mapForException.put("OtherException", e.getMessage());
        }
        return new ResponseEntity<>(mapForException, headers, 500);
    }

    @PostMapping("/signup")
    public ResponseEntity<HashMap<String, String>> createUser(@RequestBody User subscriber) {
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        HashMap<String, String> mapForException = new HashMap<>();
        // todo password 는 front 영역에서 암호화 처리한 다음 전송된다
        if (subscriber.getPassword() == null){
            mapForException.put("creating a account is failed", "password is required");
            return new ResponseEntity<>(mapForException, headers, 400);
        }
        try {
            if (new UserDao().confirmForUsable(subscriber))
                return new ResponseEntity<>(new UserDao().signUp(subscriber), headers, 201);
        } catch (SQLException e) {
            mapForException.put("SqlException", e.getMessage());
        } catch (Exception e) {
            mapForException.put("Exception", e.getMessage());
        }
        return new ResponseEntity<>(mapForException, headers, 500);
    }
}
