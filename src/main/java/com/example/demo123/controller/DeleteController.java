package com.example.demo123.controller;

import com.example.demo123.data.dao.PostDao;
import com.example.demo123.data.dto.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/api/v1/delete-api")
public class DeleteController {
    private final PostDao postDao;
    public DeleteController(PostDao postDao){
        this.postDao = postDao;
    }

    //  '저자, 글 제목, 날짜(생성 혹은 최근 수정일)'을 인자로 받음, 무분별한 삭제를 방지하고자 세 조건 모두가 명확히 주어지지 않았을 시 삭제하지 않음
    @DeleteMapping("/deletePost")
    public ResponseEntity<Void> DeletePost (@RequestBody Post post) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        HashMap<String, String> mapForException = new HashMap<>();
        try {// 3가지 인자 중 2개 이상이 주어질 시 DELETE 쿼리를 실행
            if (post.getWriter() == null)
                mapForException.put("IllegalArgumentException", "writer must be defined");
            if (post.getTitle() == null)
                mapForException.put("IllegalArgumentException", "Title must be defined");
            if (post.getCreated_date() == null)
                mapForException.put("IllegalArgumentException", "date must be defined");
            if (mapForException.size() >= 2)
                throw new IllegalArgumentException("triggered this try-catch logic");
        } catch (Exception e) {
            log.warn("at DeleteController.DeletePost: ", e);
            return new ResponseEntity<>(null, headers, 400);
        }

        try {
            // todo 서비스 레이어가 필요할 시 분리
            postDao.DeletePost(post);
            return new ResponseEntity<>(null, headers, 204);
        } catch (Exception e) {
            log.warn("at DeleteController.DeletePost: ", e);
        }
        return new ResponseEntity<>(null, headers, 500);
    }
}
