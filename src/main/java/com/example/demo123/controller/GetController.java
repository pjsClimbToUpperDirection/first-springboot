package com.example.demo123.controller;


import com.example.demo123.data.dao.PostDao;
import com.example.demo123.data.dto.Post;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

// org.springframework.web.bind.annotation
@RestController
@RequestMapping("/api/v1/get-api")
public class GetController {

    public GetController(){}


    // 조건에 해당하는 글 전부를 조회 (저자, 제목, 생성 혹은 수정일)
    // 게시글 목록을 반환하므로 반환 유형이 조금 다름 (HashMap 이 arrayList 배열의 원소로 전달됨)
    // example: http://localhost:8085/api/v1/get-api/lookUp?writer=me&title=TITLE&date=24-02-05
    @GetMapping("/lookUp")
    public ResponseEntity<ArrayList<HashMap<String, String>>> lookUpPosts(@RequestBody Post post) {
        HttpHeaders httpHeaders= new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        ArrayList<HashMap<String, String>> rowList = new ArrayList<>();
        HashMap<String, String> mapForException = new HashMap<>();
        try {
            if (post.getWriter() == null & post.getTitle() == null & post.getCreated_date() == null)
                mapForException.put("IllegalArgumentException" ,"define some argument at lease one or more");
            if (!mapForException.isEmpty())
                throw new IllegalArgumentException("triggered this try-catch logic");
        } catch (Exception e) {
            rowList.add(mapForException);
            return new ResponseEntity<>(rowList, httpHeaders, 400);
        }

        try {
            return new PostDao().lookUpPosts(post, httpHeaders);
        } catch (SQLException e) {
            mapForException.put("SqlException", e.getMessage());
        } catch (Exception e) {
            mapForException.put("OtherException", e.getMessage());
        }
        rowList.add(mapForException);
        return new ResponseEntity<>(rowList, httpHeaders, 500);
    }
}
