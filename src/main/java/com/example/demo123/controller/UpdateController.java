package com.example.demo123.controller;

import com.example.demo123.component.jwt.jwtUtil;
import com.example.demo123.data.dao.PostDao;
import com.example.demo123.data.dto.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // 4가지 인자를 전부 요구함
    @PatchMapping("/updatePost")
    public ResponseEntity<Void> UpdatePost(@RequestHeader HttpHeaders headers ,@RequestBody Post post){
        if (Objects.equals(jwtUtil.extractUsername(headers.getFirst("Authorization")), post.getWriter())) { // 'jwt 에서 추출한 이름', '요청 본문 저자' 가 같을 시 실행
            if (post.getTitle() != null && post.getContent() != null) {
                try {
                    // todo 서비스 레이어가 필요할 시 분리
                    postDao.updatePost(post);
                    return new ResponseEntity<>(null, httpHeaders, 200);
                } catch (Exception e) {
                    log.warn("at UpdateController.UpdatePosts: ", e);
                }
                return new ResponseEntity<>(null, httpHeaders, 500);
            } else {
                log.warn("original title, content that we will update must be defined");
                return new ResponseEntity<>(null, httpHeaders, 400);
            }
        } else {
            return new ResponseEntity<>(null, httpHeaders, 401);
        }
    }
}
