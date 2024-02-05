package com.example.demo123.controller;

import com.example.demo123.data.dao.PostDao;
import com.example.demo123.data.dto.Post;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/delete-api")
public class DeleteController {
    public DeleteController() {
    }

    //  저자, 글 제목을 인자로 받음, 무분별한 삭제를 방지하고자 두 조건 모두가 명확히 주어지지 않았을 시 삭제하지 않음
    @DeleteMapping("/delete/{writer}/{title}")
    public ResponseEntity<HashMap> DeletePost (@PathVariable String writer , @PathVariable String title) { // 반환 타입은 key value 배열의 value 내에 같은 유형의 배열이 중첩된 형식 반환
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        Post post = new Post();
        HashMap<String, String> mapForException = new HashMap<>();

        try {
            if (writer != null && !writer.isEmpty())
                post.setWriter(writer);
            else
                throw new IllegalArgumentException("writer must be defined");
            if (title != null && !title.isEmpty())
                post.setTitle(title);
            else
                throw new IllegalArgumentException("title must be defined");
        } catch (Exception e) {
            mapForException.put("IllegalArgumentException", e.getMessage());
            return new ResponseEntity<>(mapForException, headers, 400);
        }
        try {
            return new PostDao().DeletePost(post, headers);
        } catch (SQLException e) {
            mapForException.put("SqlException", e.getMessage());
        } catch (Exception e) {
            mapForException.put("OtherException", e.getMessage());
        }
        return new ResponseEntity<>(mapForException, headers, 500);
    }
}
