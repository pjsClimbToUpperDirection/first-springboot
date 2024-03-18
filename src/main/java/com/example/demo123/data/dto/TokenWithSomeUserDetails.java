package com.example.demo123.data.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TokenWithSomeUserDetails {
    private final String accessToken;
    private final String refreshToken;
    private final String email;
    private final String created_date;
    private final String last_modified;
}
