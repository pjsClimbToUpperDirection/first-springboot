package com.example.demo123.data.specification;

import com.example.demo123.data.entity.PostEntity;
import org.springframework.data.jpa.domain.Specification;

public class PostSpecification {
    // 기본 조건으로 구현하고 추후 and, or 조건문에 이어서 사용할 수 있다.
    public static Specification<PostEntity> equalWriter(String writer) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("writer"), writer);
    }

    public static Specification<PostEntity> equalTitle(String title) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("title"), title);
    }

    public static Specification<PostEntity> equalEmail(String email) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("email"), email);
    }
}
