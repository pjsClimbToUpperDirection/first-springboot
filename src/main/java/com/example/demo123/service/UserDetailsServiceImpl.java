package com.example.demo123.service;

import com.example.demo123.data.dao.CustomUserDao;
import com.example.demo123.data.dto.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final CustomUserDao customUserDao;
    public UserDetailsServiceImpl(CustomUserDao customUserDao){
        this.customUserDao = customUserDao;
    }
    @Override
    public UserDetails loadUserByUsername(String username) {
        // todo 추후 불필요한 try catch 구문 제거하기
        // todo 추후 Dao 에서 ResultSet 객체를 반환하지 않도록 조정하기
        try {
            ResultSet finded = customUserDao.findUserDetailsByUserName(username);
            if (!finded.next()) {
                throw new UsernameNotFoundException("not found");
            }
            CustomUserDetails customUserDetails = CustomUserDetails.builder()
                    .id(finded.getString(1))
                    .username(finded.getString(2))
                    .password(finded.getString(3))
                    .email(finded.getString(4))
                    .emailVerified(finded.getBoolean(5))
                    .locked(finded.getBoolean(6))
                    .role(finded.getString(7))
                    .build();
            finded.close();
            return customUserDetails;
        } catch (Exception e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}
