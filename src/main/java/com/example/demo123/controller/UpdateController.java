package com.example.demo123.controller;

import com.example.demo123.component.jwt.jwtUtil;
import com.example.demo123.data.dao.PostDao;
import com.example.demo123.data.dto.Post;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Objects;


@Slf4j
@RestController
@RequestMapping("/api/v1/patch-api")
public class UpdateController {
    private final PostDao postDao;
    private final HttpHeaders httpHeaders;
    private final jwtUtil jwtUtil;
    public UpdateController(PostDao postDao, HttpHeaders httpHeaders, jwtUtil jwtUtil){
        this.postDao = postDao;
        this.httpHeaders = httpHeaders;
        this.jwtUtil = jwtUtil;
    }

    @PatchMapping("/updatePost")
    public ResponseEntity<Void> UpdatePost(@RequestHeader HttpHeaders headers ,@RequestBody Post post){
        String username;
        // 토큰 만료 여부와 상관없이 사용자 이름을 추출하도록 구현됨
        try {
            username = jwtUtil.extractUsername(headers.getFirst("Authorization")); // jwt 에서 사용자 이름 추출
        } catch (ExpiredJwtException e) {
            username = e.getClaims().getSubject();
        }
        if (post.getTitle() != null && post.getContent() != null) {
            try {
                // todo 서비스 레이어가 필요할 시 분리
                post.setWriter(username);
                postDao.updatePost(post);
                return new ResponseEntity<>(null, httpHeaders, 200);
            } catch (Exception e) {
                log.warn("at UpdateController.UpdatePosts: ", e);
                return new ResponseEntity<>(null, httpHeaders, 500);
            }
        } else {
            return new ResponseEntity<>(null, httpHeaders, 400);
        }
    }
}
