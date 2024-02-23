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
        String findedRefreshToken;
        try {
            findedRefreshToken = tokenDao.findRefreshToken(refreshToken);
            return findedRefreshToken;
        } catch (Exception e) {

        }
        return "";
    }
}
