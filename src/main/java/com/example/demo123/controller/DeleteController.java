package com.example.demo123.controller;

import com.example.demo123.data.entity.PostEntity;
import com.example.demo123.data.repository.PostRepository;
import com.example.demo123.data.specification.PostSpecification;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/v1/delete-api")
public class DeleteController {

    // 해당 인터페이스에서 상속받은 요소들을 확인해 보자
    private final PostRepository postRepository;
    @Autowired // 필드 주입이 권장되지 않음, 생성자 인자에 자동와이어링
    public DeleteController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @DeleteMapping("/delete/{writer}/{title}")
    public ResponseEntity<HashMap<String, HashMap<String, String>>> DeletePost (@PathVariable String writer , @PathVariable String title){ // 반환 타입은 key value 배열의 value 내에 같은 유형의 배열이 중첩된 형식 반환
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        Specification<PostEntity> DeleteList = (root, query, criteriaBuilder) -> null;

        if(writer != null && !writer.isEmpty())
            DeleteList = DeleteList.and(PostSpecification.equalWriter(writer));
        else
            throw new IllegalArgumentException("writer must be defined");
        if(title != null && !title.isEmpty())
            DeleteList = DeleteList.and(PostSpecification.equalTitle(title));
        else
            throw new IllegalArgumentException("title must be defined");

        try {
            List<PostEntity> Deleted = postRepository.findAll(DeleteList); // 응답 본문을 위한 코드로 그 이상의 목적은 따로 없음
            postRepository.delete(DeleteList);
            return new ResponseEntity<>(resBody(Deleted, null), headers, 201);
        } catch (Exception e) {
            return new ResponseEntity<>(resBody(null, e), headers, 500);
        }
    }

    private HashMap<String, HashMap<String, String>> resBody(@Nullable List<PostEntity> Posts, @Nullable Exception e) {
        if(Posts != null && e == null) {
            HashMap<String, String> postHashMap = new HashMap<>();
            for(PostEntity post : Posts) {
                postHashMap.put(post.getWriter(), post.getTitle());
            }
            HashMap<String, HashMap<String, String>> Deleted = new HashMap<>();
            Deleted.put("Deleted", postHashMap);
            return Deleted;
        } else if (e != null && Posts == null) {
            HashMap<String, String> ExceptionHashMap = new HashMap<>();
            ExceptionHashMap.put(e.getClass().toString(), e.getMessage());
            HashMap<String, HashMap<String, String>> Exception = new HashMap<>();
            Exception.put("Exception", ExceptionHashMap);
            return Exception;
        }
        throw new IllegalArgumentException("parameters is not valid");
    }
}
