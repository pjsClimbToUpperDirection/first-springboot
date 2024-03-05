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
@RequestMapping("/api/v1/delete-api")
public class DeleteController {
    private final PostDao postDao;
    private final HttpHeaders httpHeaders;
    private final jwtUtil jwtUtil;
    public DeleteController(PostDao postDao, HttpHeaders httpHeaders, jwtUtil jwtUtil){
        this.postDao = postDao;
        this.httpHeaders = httpHeaders;
        this.jwtUtil = jwtUtil;
    }

    //  '저자, 글 제목, 날짜(생성 혹은 최근 수정일)'을 인자로 받음, 무분별한 삭제를 방지하고자 세 조건 모두가 명확히 주어지지 않았을 시 삭제하지 않음
    @DeleteMapping("/deletePost")
    public ResponseEntity<Void> DeletePost (@RequestHeader HttpHeaders headers, @RequestBody Post post) {
        if (Objects.equals(jwtUtil.extractUsername(headers.getFirst("Authorization")), post.getWriter())) { // 'jwt 에서 추출한 이름', '요청 본문 저자' 가 같을 시 실행
            if ((post.getWriter() != null && post.getTitle() != null) || (post.getWriter() != null && post.getCreated_date() != null)) {
                try {
                    // todo 서비스 레이어가 필요할 시 분리
                    postDao.DeletePost(post);
                    return new ResponseEntity<>(null, httpHeaders, 204);
                } catch (Exception e) {
                    log.warn("at DeleteController.DeletePost: ", e);
                }
                return new ResponseEntity<>(null, httpHeaders, 500);
            } else {
                log.warn("writer and one of title and date must be defined");
                return new ResponseEntity<>(null, httpHeaders, 400);
            }
        } else {
            return new ResponseEntity<>(null, httpHeaders, 401);
        }
    }
}
