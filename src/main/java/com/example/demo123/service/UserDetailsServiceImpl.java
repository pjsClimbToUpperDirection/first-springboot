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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            ResultSet finded = new CustomUserDao().findUserDetailsByUserName(username);
            if (!finded.next())
                throw new UsernameNotFoundException("not found");
            return CustomUserDetails.builder()
                    .id(finded.getString(0))
                    .username(finded.getString(1))
                    .password(finded.getString(2))
                    .email(finded.getString(3))
                    .emailVerified(finded.getBoolean(4))
                    .locked(finded.getBoolean(5))
                    .role(finded.getString(6))
                    .build();
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("not found");
        } catch (Exception e) {
            throw new UsernameNotFoundException("inner Error");
        }
    }
}
