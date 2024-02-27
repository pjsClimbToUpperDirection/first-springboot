package com.example.demo123.controller;


import com.example.demo123.component.Translater;
import com.example.demo123.data.dao.PostDao;
import com.example.demo123.data.dto.Post;
import com.example.demo123.service.UserDetailsServiceImpl;
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
    private final UserDetailsServiceImpl userDetailsService;
    private final Translater translater;
    public GetController(HttpHeaders httpHeaders, PostDao postDao, Translater translater, UserDetailsServiceImpl userDetailsService){
        this.httpHeaders = httpHeaders;
        this.postDao = postDao;
        this.translater = translater;
        this.userDetailsService = userDetailsService;
    }


    // 조건에 해당하는 글 전부를 조회 (저자, 제목, 생성 혹은 수정일), 이후 해당하는 게시글 목록을 반환
    @GetMapping("/lookUp")
    public ResponseEntity<ArrayList<Post>> lookUpPosts(@RequestBody Post post) {
        try {
            if (post.getWriter() == null && post.getTitle() == null && post.getCreated_date() == null)
                throw new IllegalArgumentException("define some argument at lease one or more");
        } catch (Exception e) {
            log.warn("at GetController.lookUpPosts: ", e);
            return new ResponseEntity<>(null, httpHeaders, 400);
        }

        try { // todo 서비스 레이어가 필요할 시 Dao 분리
            return new ResponseEntity<>(translater.ForResultSet(postDao.lookUpPosts(post)), httpHeaders, 200);
        } catch (Exception e) {
            log.warn("at GetController.lookUpPosts: ", e);
            return new ResponseEntity<>(null, httpHeaders, 500);
        }
    }
}
