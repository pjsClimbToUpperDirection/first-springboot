package com.example.demo123.controller;

import com.example.demo123.data.dao.PostDao;
import com.example.demo123.data.dto.Post;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/update-api")
public class UpdateController {

    public UpdateController(){}

    // 4가지 인자를 전부 요구함
    @PatchMapping("/updatePosts")
    public ResponseEntity<HashMap<String, String>> UpdatePosts(@RequestBody JsonNode updateObj){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        HashMap<String, String> mapForException = new HashMap<>();

        ObjectMapper mapper = new ObjectMapper();
        Post selected;
        Post updated;
        try {
            selected = mapper.treeToValue(updateObj.get("selected"), Post.class);
            updated =  mapper.treeToValue(updateObj.get("updated"), Post.class);

            try {
                if (selected.getWriter() == null)
                    mapForException.put("IllegalArgumentException-writer", "writer must be defined");
                if (selected.getTitle() == null)
                    mapForException.put("IllegalArgumentException-title", "Title must be defined");
                if (updated.getTitle() == null)
                    mapForException.put("IllegalArgumentException-updatedTitle", "which is updated as a newTitle must be defined");
                if (updated.getContent() == null)
                    mapForException.put("IllegalArgumentException-updatedContent", "which is updated as a newContent must be defined");
                if (!mapForException.isEmpty())
                    throw new IllegalArgumentException("triggered this try-catch logic");
            } catch (Exception e) {
                return new ResponseEntity<>(mapForException, headers, 400);
            }

        } catch (Exception e) {
            mapForException.put("Exception in process of Deserialization", e.getMessage());
            return new ResponseEntity<>(mapForException, headers, 500);
        }
        try {
            return new PostDao().updatePosts(selected, updated, headers);
        } catch (SQLException e) {
            mapForException.put("SqlException", e.getMessage());
        } catch (Exception e) {
            mapForException.put("OtherException", e.getMessage());
        }
        return new ResponseEntity<>(mapForException, headers, 500);
    }
}
