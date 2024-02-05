package com.example.demo123.controller;


import com.example.demo123.data.dao.PostDao;
import com.example.demo123.data.dto.Post;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

// org.springframework.web.bind.annotation
@RestController
@RequestMapping("/api/v1/get-api")
public class GetController {

    public GetController(){}


    // 조건에 해당하는 글 전부를 조회 (저자, 제목, 생성 혹은 수정일)
    // 인자가 둘중 하나 혹은 전부 제공되었을 시 dao를 통해 접근, 본 메서드에서 유효성 검사 수행
    // example: http://localhost:8085/api/v1/get-api/lookUp?writer=me&title=TITLE&date=24-02-05
    @GetMapping("/lookUp")
    public ResponseEntity<HashMap> lookUpPosts(@RequestParam Map<String, String> params) {
        HttpHeaders httpHeaders= new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        Post post = new Post();
        new setDto().multipurposeDTO(params, post);

        HashMap<String, String> mapForException = new HashMap<>();
        try {
            if (post.getWriter() == null & post.getTitle() == null & post.getCreated_date() == null)
                mapForException.put("IllegalArgumentException" ,"define some argument at lease one or more");
            if (!mapForException.isEmpty())
                throw new IllegalArgumentException("triggered this try-catch logic");
        } catch (Exception e) {
            return new ResponseEntity<>(mapForException, httpHeaders, 400);
        }

        try {
            return new PostDao().lookUpPosts(post, httpHeaders);
        } catch (SQLException e) {
            mapForException.put("SqlException", e.getMessage());
        } catch (Exception e) {
            mapForException.put("OtherException", e.getMessage());
        }
        return new ResponseEntity<>(mapForException, httpHeaders, 500);
    }
}
