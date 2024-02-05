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

@RestController
@RequestMapping("/api/v1/update-api")
public class UpdateController {

    public UpdateController(){}

    @PatchMapping("/updatePosts")
    public ResponseEntity<HashMap> UpdatePosts(@RequestParam Map<String, String> params){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        Post SelectedPost = new Post();
        Post UpdatedPost = new Post();
        HashMap<String, String> mapForException = new HashMap<>();
        try {
            params.forEach((key, value) -> {
                switch (key) {
                    case "writer" -> {
                        SelectedPost.setWriter(value);
                    }
                    case "title" -> {
                        SelectedPost.setTitle(value);
                    }
                    case "newTitle" -> {
                        UpdatedPost.setTitle(value);
                    }
                    case "newContent" ->{
                        UpdatedPost.setContent(value);
                    }
                    default ->
                            System.out.println("unKnown value");
                }
            });
            if (SelectedPost.getWriter() == null)
                mapForException.put("IllegalArgumentException", "writer must be defined");
            if (SelectedPost.getTitle() == null)
                mapForException.put("IllegalArgumentException", "Title must be defined");
            if (UpdatedPost.getTitle() == null)
                mapForException.put("IllegalArgumentException", "which is updated as a newPosts must be defined");
            if (UpdatedPost.getContent() == null)
                mapForException.put("IllegalArgumentException", "which is updated as a newContent must be defined");
            if (!mapForException.isEmpty())
                throw new IllegalArgumentException("triggered this try-catch logic");
        } catch (Exception e) {
            return new ResponseEntity<>(mapForException, headers, 400);
        }
        try {
            return new PostDao().updatePosts(SelectedPost, UpdatedPost, headers);
        } catch (SQLException e) {
            mapForException.put("SqlException", e.getMessage());
        } catch (Exception e) {
            mapForException.put("OtherException", e.getMessage());
        }
        return new ResponseEntity<>(mapForException, headers, 500);
    }
}
