package com.example.demo123.controller;


import com.example.demo123.component.jwt.jwtUtil;
import com.example.demo123.data.dao.PostDao;
import com.example.demo123.data.dto.Post;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

// org.springframework.web.bind.annotation
@Slf4j
@RestController
@RequestMapping("/api/v1/get-api")
public class GetController {
    private final HttpHeaders httpHeaders;
    private final PostDao postDao;
    private final jwtUtil jwtUtil;
    public GetController(HttpHeaders httpHeaders, PostDao postDao, jwtUtil jwtUtil){
        this.httpHeaders = httpHeaders;
        this.postDao = postDao;
        this.jwtUtil = jwtUtil;
    }


    // 조건에 해당하는 글 전부를 조회 (저자, 제목, 생성 혹은 수정일), 이후 해당하는 게시글 목록을 반환
    // title(제목)은 고유한 값이므로 오직 하나의 행이 조회된다
    @GetMapping("/lookUpAll")
    public ResponseEntity<ArrayList<Post>> lookUpAllPosts(@RequestHeader HttpHeaders headers) {
        String username;
        // 토큰 만료 여부와 상관없이 사용자 이름을 추출하도록 구현됨
        try {
            username = jwtUtil.extractUsername(headers.getFirst("Authorization")); // jwt 에서 사용자 이름 추출
        } catch (ExpiredJwtException e) {
            username = e.getClaims().getSubject();
        }
        if (username.length() < 3) { // 이외에 제목, 생성 혹은 수정일도 조건으로 첨부 가능
            return new ResponseEntity<>(null, httpHeaders, 400);
        } else {
            try { // todo 서비스 레이어가 필요할 시 Dao 분리
                Post post = new Post();
                post.setWriter(username);
                return new ResponseEntity<>(postDao.lookUpPosts(post), httpHeaders, 200);
            } catch (Exception e) {
                log.warn("at GetController.lookUpPosts: ", e);
                return new ResponseEntity<>(null, httpHeaders, 500);
            }
        }
    }

    /*
    @GetMapping("/lookUp")
    public ResponseEntity<ArrayList<Post>> lookUpPost(@RequestHeader HttpHeaders headers, @RequestParam("title") String title) {
        String username;
        // 토큰 만료 여부와 상관없이 사용자 이름을 추출하도록 구현됨
        try {
            username = jwtUtil.extractUsername(headers.getFirst("Authorization")); // jwt 에서 사용자 이름 추출
        } catch (ExpiredJwtException e) {
            username = e.getClaims().getSubject();
        }
        if (username.length() < 3 && title.isEmpty()) { // 이외에 제목, 생성 혹은 수정일도 조건으로 첨부 가능
            return new ResponseEntity<>(null, httpHeaders, 400);
        } else {
            try { // todo 서비스 레이어가 필요할 시 Dao 분리
                Post post = new Post();
                post.setWriter(username);
                post.setTitle(title);
                return new ResponseEntity<>(postDao.lookUpPosts(post), httpHeaders, 200);
            } catch (Exception e) {
                log.warn("at GetController.lookUpPosts: ", e);
                return new ResponseEntity<>(null, httpHeaders, 500);
            }
        }
    }
    */
}
