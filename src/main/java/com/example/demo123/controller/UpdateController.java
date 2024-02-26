package com.example.demo123.controller;

import com.example.demo123.data.dao.PostDao;
import com.example.demo123.data.dto.Post;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/api/v1/patch-api")
public class UpdateController {
    private final PostDao postDao;
    private final HttpHeaders httpHeaders;
    public UpdateController(PostDao postDao, HttpHeaders httpHeaders){
        this.postDao = postDao;
        this.httpHeaders = httpHeaders;
    }

    // 4가지 인자를 전부 요구함
    @PatchMapping("/updatePosts")
    public ResponseEntity<Void> UpdatePosts(@RequestBody JsonNode updateObj){ // todo 해당 인자의 dto 정의하기
        // 역직렬화를 위한 객체 선언;
        ObjectMapper mapper = new ObjectMapper();
        Post selected;
        Post updated;

        try {
            selected = mapper.treeToValue(updateObj.get("selected"), Post.class);
            updated =  mapper.treeToValue(updateObj.get("updated"), Post.class);

            try {
                if (selected.getWriter() == null && selected.getTitle() == null)
                    throw new IllegalArgumentException("writer and title that is reference of previous Post must be defined");
                if (updated.getTitle() == null && updated.getContent() == null)
                    throw new IllegalArgumentException("which is updated as a newTitle must be defined");
            } catch (Exception e) {
                log.warn("at UpdateController.UpdatePosts: ", e);
                return new ResponseEntity<>(null, httpHeaders, 400);
            }

        } catch (Exception e) {
            log.warn("at UpdateController.UpdatePosts: ", e);
            return new ResponseEntity<>(null, httpHeaders, 500);
        }
        try {
            // todo 서비스 레이어가 필요할 시 분리
            postDao.updatePosts(selected, updated);
            return new ResponseEntity<>(null, httpHeaders, 200);
        } catch (Exception e) {
            log.warn("at UpdateController.UpdatePosts: ", e);
        }
        return new ResponseEntity<>(null, httpHeaders, 500);
    }

}
