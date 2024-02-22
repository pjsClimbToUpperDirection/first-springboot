package com.example.demo123.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
public class Token {
    private final String accessToken;
    private final String refreshToken;
}
