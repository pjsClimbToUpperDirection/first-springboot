package com.example.demo123.service;

import jakarta.mail.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.Properties;


import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;


@Slf4j
@Service
@PropertySource("classpath:application.properties")
public class MailSendingService {
    public MailSendingService(){}

    @Value("${mail.config.host}")
    private String host;

    @Value("${mail.config.user}")
    private String user;

    @Value("${mail.config.password}")
    private String password;

    // 원한다면 추후 가능을 확장하여 여러 요소를 집어넣어 다양한 내용을 전송할수 있도록 하거나, 디자인 요소를 가미한 배경을 첨부할수도 있음
    public void sendSimpleMail(String emailAddress, String subject, String mainText) {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", 587);
        props.put("mail.smtp.auth", "true");
        Session session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            // 추후 프로퍼티 파일에서 값을 가져오도록 하기
            msg.setFrom(new InternetAddress(user, "tech blog admin"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(emailAddress, "Mr. User who is using this blog"));
            msg.setSubject(subject);
            msg.setText(mainText);
            Transport.send(msg);
        } catch (AddressException e) {
            log.warn("Problem in MailAddress ", e);
        } catch (MessagingException e) {
            log.warn("Problem in Messaging ", e);
        } catch (UnsupportedEncodingException e) {
            log.warn("Encoding is not support ", e);
        } catch (Exception e) {
            log.error("unKnown Exception in MailSendingService ", e);
        }
    }
}
