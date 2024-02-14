package com.example.demo123.service;

import com.example.demo123.data.dao.CustomUserDao;
import com.example.demo123.data.dto.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // todo CustomUserDao 접근 로직 작성
        try {
            ResultSet finded = new CustomUserDao().findUserDetailsByUserName(username);
            if (!finded.next())
                throw new UsernameNotFoundException("not found");
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("not found");
        } catch (Exception e) {
            throw new UsernameNotFoundException("inner Error");
        }

        CustomUserDetails customUserDetails = new CustomUserDetails();
        // todo 해당 영역에 dto 값 설정
        return customUserDetails;
    }

    private HashMap<String, String> findedUser(ResultSet selectedRow) throws Exception{
        String[] columns = {"post_id", "writer", "email", "title", "content", "created_date", "updated_date"};
        HashMap<String, String> finded = new HashMap<>();

        for (String column : columns) {
            finded.put(column, selectedRow.getString(column));
        }
        System.out.println("SELECTED_USER -> " + finded);
        return finded;
    }
}
