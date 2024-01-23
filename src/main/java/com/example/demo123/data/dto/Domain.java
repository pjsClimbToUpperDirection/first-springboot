package com.example.demo123.data.dto;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Domain {
    @Id
    private String id;
    @NotNull
    private String host;
    @NotNull
    private type type;
    @NotNull
    private UUID clientSecret;;
}
// enum 사용하여 할당 가능 값들을 미리 정의하여 타입 안정성 보장
enum type {
    free, premiums
}