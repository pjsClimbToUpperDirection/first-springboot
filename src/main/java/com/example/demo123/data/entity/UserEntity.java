package com.example.demo123.data.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Date;
import java.time.LocalDateTime;

// 엔티티는 데이터베이스의 테이블에 대응한다
// 테이블의 구조 (column, 해당 colunm 에 저장되어야 할 데이터, 제약사항) 설계
@Entity
@Getter
@Setter
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class) // 콜백될 클래스를 지정하는 에너테이션 -> 여기서는 AuditingEntityListener  클래스가 콜백 리스너로 지정되어 Entity 에서 이벤트 발생 시 특정 로직 수행
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String password;
    private String nickname;
    private Provider provider;
    private String snsId;
    @CreatedDate
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate; // 업데이트시 본 열의 값을 갱신하는 로직은 따로 구현할 것(사용자 정보 최종 갱신 일자를 저장하여 활용할수 있을듯 하다)
    private LocalDateTime deletedAt; // 삭제 일자 -> 배치 처리를 통해 삭제된(deletedAt !== null 인) row 삭제하는 배치 처리 구현해 보기(사용자 계정 삭제 여부 확인 및, 일시 확인 등으로 사용가능)

    enum Provider{
        kakao, local
    }
}
