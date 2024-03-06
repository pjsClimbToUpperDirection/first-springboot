package com.example.demo123.service;

import com.example.demo123.data.dao.RedisDao;
import com.example.demo123.data.dto.controller.AuthNumberVerification;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class AuthenticationNumberCreator {
    Random random = new Random();
    private final RedisDao redisDao;
    private final MailSendingService mailSendingService;

    public AuthenticationNumberCreator(RedisDao redisDao, MailSendingService mailSendingService){
        this.redisDao = redisDao;
        this.mailSendingService = mailSendingService;
    }

    public void AuthNumberCreation(String receiver_s_address, String subject, String username) {
        int randomNum1 = random.nextInt(1000);
        int randomNum2 = random.nextInt(1000);
        redisDao.setValues(randomNum1 + "-" + randomNum2 , username, 70); // 70초 (임시 저장된 계정 정보는 90초)
        mailSendingService.sendSimpleMail(receiver_s_address, subject, "발급된 인증번호는: " + randomNum1 + "-" + randomNum2);
    }

    public String AuthNumberVerifier(AuthNumberVerification authNumberVerification) {
        // 인증번호가 조회될 시 본문(username) 반환
        return redisDao.getValues(authNumberVerification.getNum1() + "-" + authNumberVerification.getNum2());
    }
}
