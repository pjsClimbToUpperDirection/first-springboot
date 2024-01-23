package com.example.demo123.data.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


// 엔티티는 데이터베이스의 테이블에 대응한다
// 테이블의 구조 (column, 해당 colunm 에 저장되어야 할 데이터, 제약사항) 설계
@Entity
@Getter
@Setter
@Table(name = "posts")
@EntityListeners(AuditingEntityListener.class) // 콜백될 클래스를 지정하는 에너테이션 -> 여기서는 AuditingEntityListener  클래스가 콜백 리스너로 지정되어 Entity 에서 이벤트 발생 시 특정 로직 수행
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String writer;
    private String title;
    private String content;
    private String img;
    private String email;

    @CreatedDate
    @Column(updatable = false) // 초기값 할당 이후 변경될수 없다
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

}
