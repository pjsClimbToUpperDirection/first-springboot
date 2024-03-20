package com.example.demo123.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
@PropertySource("classpath:application.properties")
public class EncodingConfig {

    @Value("${bCryptPasswordEncoder.strength}")
    private Integer strength;
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(strength);
    }
}
