package com.example.demo123.data.repository;

import com.example.demo123.data.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> { // <T,ID> T -> repository 가 관리하는 domain type, ID -> repository 가 관리하는 entity ID(기본키)의 유형
}
