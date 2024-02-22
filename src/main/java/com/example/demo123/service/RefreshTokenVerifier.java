package com.example.demo123.service;

import com.example.demo123.data.dao.TokenDao;
import org.springframework.stereotype.Service;


@Service
public class RefreshTokenVerifier {
    private final TokenDao tokenDao;

    public RefreshTokenVerifier(TokenDao tokenDao){
        this.tokenDao = tokenDao;
    }

    public String verification(String refreshToken) {
        try {
            return tokenDao.findRefreshToken(refreshToken).getString(1);
        } catch (Exception e) {

        }
        return "";
    }
}
