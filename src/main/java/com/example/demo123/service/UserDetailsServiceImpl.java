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
    @Override
    public UserDetails loadUserByUsername(String username) {
        try {
            ResultSet finded = new CustomUserDao().findUserDetailsByUserName(username);
            if (!finded.next())
                throw new UsernameNotFoundException("not found");
            return CustomUserDetails.builder()
                    .id(finded.getString(1))
                    .username(finded.getString(2))
                    .password(finded.getString(3))
                    .email(finded.getString(4))
                    .emailVerified(finded.getBoolean(5))
                    .locked(finded.getBoolean(6))
                    .role(finded.getString(7))
                    .build();
        } catch (Exception e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}
