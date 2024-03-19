package com.example.demo123.controller;

import com.example.demo123.component.jwt.jwtUtil;
import com.example.demo123.data.dao.PostDao;
import com.example.demo123.data.dto.Post;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;


@Slf4j
@RestController
@RequestMapping("/api/v1/delete-api")
public class DeleteController {
    private final PostDao postDao;
    private final HttpHeaders httpHeaders;
    private final jwtUtil jwtUtil;
    public DeleteController(PostDao postDao, HttpHeaders httpHeaders, jwtUtil jwtUtil){
        this.postDao = postDao;
        this.httpHeaders = httpHeaders;
        this.jwtUtil = jwtUtil;
    }

    // 요청 본문에는 게시글의 제목만 요구됨
    @DeleteMapping("/deletePost")
    public ResponseEntity<Void> DeletePost (@RequestHeader HttpHeaders headers, @RequestBody Post post) { // title 만 요구됨
        String username;
        try {
            username = jwtUtil.extractUsername(headers.getFirst("Authorization")); // jwt 에서 사용자 이름 추출
        } catch (ExpiredJwtException e) {
            username = e.getClaims().getSubject();
        }
        if (username != null && post.getTitle() != null) {
            post.setWriter(username);
            try {
                // todo 서비스 레이어가 필요할 시 분리
                postDao.DeletePost(post);
                return new ResponseEntity<>(null, httpHeaders, 204);
            } catch (Exception e) {
                log.warn("at DeleteController.DeletePost: ", e);
            }
            return new ResponseEntity<>(null, httpHeaders, 500);
        } else {
            return new ResponseEntity<>(null, httpHeaders, 400);
        }
    }
}
