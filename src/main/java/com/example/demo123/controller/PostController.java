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
import java.util.Map;

@RestController
@RequestMapping("/api/v1/post-api")
public class PostController {

    public PostController() {}

    // 한번에 하나의 포스트만 업로드 가능
    // 예시 -> http://localhost:8085/api/v1/post-api/uploadPost?email=eerI@gmail.com&content=Aop&writer=me
    @PostMapping("/uploadPost")
    public ResponseEntity<HashMap<String, String>> UploadPost(@RequestParam Map<String, String> params){
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
                        case "writer must be defined" ->
                            map.put("writer", e.toString());
                        case "content must be defined" ->
                            map.put("content", e.toString());
                        case "email address must be defined" ->
                            map.put("email", e.toString());
                        case "title must be defined" ->
                            map.put("title", e.toString());
                        default -> {}
                    }
                }
            }
            return new ResponseEntity<>(map, headers, 400);
        } else {
            HashMap<String, String> mapForException = new HashMap<>();

            new setPostDto().multipurposeSetter(params, post);
            try {
                return new PostDao().InsertPost(post, headers);
            } catch (SQLException e) {
                mapForException.put("SqlException", e.getMessage());
            } catch (Exception e) {
                mapForException.put("OtherException", e.getMessage());
            }
            return new ResponseEntity<>(mapForException, headers, 500);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<HashMap<String, String>> createUser(@RequestBody User subscriber) {
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        User user = new User();

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
