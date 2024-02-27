package com.example.demo123.controller;

import com.example.demo123.data.dao.PostDao;
import com.example.demo123.data.dto.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/v1/post-api")
public class PostController {
    private final PostDao postDao;
    private final HttpHeaders httpHeaders;
    public PostController(PostDao postDao, HttpHeaders httpHeaders) {
        this.postDao = postDao;
        this.httpHeaders = httpHeaders;
    }

    // 한번에 하나의 포스트만 업로드 가능
    // 예시 -> http://localhost:8085/api/v1/post-api/uploadPost?email=eerI@gmail.com&content=Aop&writer=me
    @PostMapping("/uploadPost")
    public ResponseEntity<Void> UploadPost(@RequestBody Post post){
        try {
            if (post.getWriter() == null || post.getContent() == null || post.getEmail() == null || post.getTitle() == null) {
                throw new IllegalArgumentException("one of required argument is null");
            }
            if (!post.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")){ // @ 앞에 대소문자 알파벳, 0~9까지의 숫자, 언더바,점,하이픈 허용됨, 뒤로는 하나 이상의 어떠한 문자가 존재해야 함
                throw new IllegalArgumentException("this -email- arguments is not suitable on the required format");
            }
        } catch (Exception e) {
            log.warn("at PostController.UploadPosts: ", e);
            return new ResponseEntity<>(null, httpHeaders, 400);
        }

        try {
            // todo 서비스 레이어가 필요할 시 분리
            postDao.InsertPost(post);
            return new ResponseEntity<>(null, httpHeaders, 201);
        } catch (Exception e) {
            log.warn("at PostController.UploadPosts: ", e);
        }
        return new ResponseEntity<>(null, httpHeaders, 500);
    }
}
