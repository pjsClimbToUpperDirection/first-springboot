package com.example.demo123.service;

import com.example.demo123.data.dao.CustomUserDao;
import com.example.demo123.data.dto.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final CustomUserDao customUserDao;
    public UserDetailsServiceImpl(CustomUserDao customUserDao){
        this.customUserDao = customUserDao;
    }
    @Override
    public CustomUserDetails loadUserByUsername(String username) {
        try {
            return customUserDao.findUserDetailsByUserName(username);
        } catch (Exception e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}
