package com.example.demo123.controller;

import com.example.demo123.data.dao.PostDao;
import com.example.demo123.data.dto.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/v1/delete-api")
public class DeleteController {
    private final PostDao postDao;
    private final HttpHeaders httpHeaders;
    public DeleteController(PostDao postDao, HttpHeaders httpHeaders){
        this.postDao = postDao;
        this.httpHeaders = httpHeaders;
    }

    //  '저자, 글 제목, 날짜(생성 혹은 최근 수정일)'을 인자로 받음, 무분별한 삭제를 방지하고자 세 조건 모두가 명확히 주어지지 않았을 시 삭제하지 않음
    @DeleteMapping("/deletePost")
    public ResponseEntity<Void> DeletePost (@RequestBody Post post) {
        try { // 3가지 인자 중 2개 이상이 주어질 시 DELETE 쿼리를 실행
            if ((post.getWriter() == null && post.getTitle() == null) || (post.getWriter() == null && post.getCreated_date() == null) || (post.getTitle() == null && post.getCreated_date() == null))
                throw new IllegalArgumentException("argument must be defined at least two or more");
        } catch (Exception e) {
            log.warn("at DeleteController.DeletePost: ", e);
            return new ResponseEntity<>(null, httpHeaders, 400);
        }

        try {
            // todo 서비스 레이어가 필요할 시 분리
            postDao.DeletePost(post);
            return new ResponseEntity<>(null, httpHeaders, 204);
        } catch (Exception e) {
            log.warn("at DeleteController.DeletePost: ", e);
        }
        return new ResponseEntity<>(null, httpHeaders, 500);
    }
}
