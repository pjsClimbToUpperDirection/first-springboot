package com.example.demo123.controller;

import com.example.demo123.component.jwt.jwtUtil;
import com.example.demo123.data.dao.PostDao;
import com.example.demo123.data.dto.Post;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/v1/post-api")
public class PostController {
    private final PostDao postDao;
    private final HttpHeaders headers;
    private final jwtUtil jwtUtil;
    public PostController(PostDao postDao, HttpHeaders headers, jwtUtil jwtutil) {
        this.postDao = postDao;
        this.headers = headers;
        this.jwtUtil = jwtutil;
    }

    // 한번에 하나의 포스트만 업로드 가능
    // todo writer 는 추후 토큰에서 사용자 이름을 추출하여 추가하도록 구현할 것
    @PostMapping("/uploadPost")
    public ResponseEntity<Void> UploadPost(@RequestBody Post post){
        String username;
        // 토큰 만료 여부와 상관없이 사용자 이름을 추출하도록 구현됨
        try {
            username = jwtUtil.extractUsername(headers.getFirst("Authorization")); // jwt 에서 사용자 이름 추출
        } catch (ExpiredJwtException e) {
            username = e.getClaims().getSubject();
        }
        if (post.getContent() == null || post.getTitle() == null) {
            return new ResponseEntity<>(null, headers, 400);
        }
        try {
            // todo 서비스 레이어가 필요할 시 분리
            post.setWriter(username);
            postDao.InsertPost(post);
            return new ResponseEntity<>(null, headers, 201);
        } catch (Exception e) {
            log.warn("at PostController.UploadPosts: ", e);
        }
        return new ResponseEntity<>(null, headers, 500);
    }
}
