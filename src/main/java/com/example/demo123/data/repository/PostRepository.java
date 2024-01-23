package com.example.demo123.data.repository;

import com.example.demo123.data.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// JpaRepository -> Repository 의 JPA 특화 확장판

// 데이터 접근 객체
// Entity 로 구성된 DB에 접근하는 메서드들(CRUD) 을 사용하기 위한 인터페이스
// <T,ID> T -> repository 가 관리하는 domain type, ID -> repository 가 관리하는 entity ID(기본키)의 유형
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    List<PostEntity> findByWriter(String writer);
}
