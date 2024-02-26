package com.example.demo123.data.dto.controller;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Token {
    private final String accessToken;
    private final String refreshToken;
}
