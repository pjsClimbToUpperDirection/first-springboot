package com.example.demo123.data.dao;

import com.example.demo123.config.DbConfig;
import com.example.demo123.data.dto.CustomUserDetails;
import com.example.demo123.data.dto.controller.UserForm;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

@Repository
public class CustomUserDao {
    public CustomUserDao(){}

    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(DbConfig.class);
    DataSource dataSource = ctx.getBean("dataSource", DataSource.class);

    Date now = new Date();
    SimpleDateFormat date = new SimpleDateFormat("yy-MM-dd-HH-mm"); // string 타입


    public CustomUserDetails findUserDetailsByUserName(String username) throws Exception {
        ResultSet resultSet;
        Connection connection;
        connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM userdetails WHERE username = ?");
        statement.setString(1, username);
        resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return CustomUserDetails.builder()
                    .id(resultSet.getString(1))
                    .username(resultSet.getString(2))
                    .password(resultSet.getString(3))
                    .email(resultSet.getString(4))
                    .emailVerified(resultSet.getBoolean(5))
                    .locked(resultSet.getBoolean(6))
                    .role(resultSet.getString(7))
                    .created_date(resultSet.getString(8))
                    .last_modified(resultSet.getString(9))
                    .build();
        }
        return null;
    }

    public void createUser(CustomUserDetails customUserDetails) throws Exception {
        Connection connection;
        connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("INSERT INTO userdetails (username, password, email, emailVerified, locked, role, created_date) values (?, ?, ?, ?, ?, ?, ?)");
        statement.setString(1, customUserDetails.getUsername());
        statement.setString(2, customUserDetails.getPassword());
        statement.setString(3, customUserDetails.getEmail());
        statement.setBoolean(4, customUserDetails.isEmailVerified());
        statement.setBoolean(5, false);
        statement.setString(6, "u");
        statement.setString(7, date.format(now));
        statement.executeUpdate();
        connection.close();
    }

    public String confirmPassword(UserForm userForm) throws Exception {
        String password = null;
        Connection connection;
        connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT password From userdetails WHERE username = ?");
        statement.setString(1, userForm.getUsername());
        ResultSet passwordConfirmation = statement.executeQuery();
        if (passwordConfirmation.next()) {
            password = passwordConfirmation.getString(1);
        }
        passwordConfirmation.close();
        return password;
    }

    public void ModifyPassword(UserForm userForm) throws Exception {
        Connection connection;
        connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("UPDATE userdetails SET password = ?, last_modified_date = ? WHERE username = ?");
        statement.setString(1, userForm.getPassword()); // 변경될 pw
        statement.setString(2, date.format(now) + "_00"); // 최종 수정일자 (비밀번호)
        statement.setString(3, userForm.getUsername());
        statement.executeUpdate();
        connection.close();
    }

    public void ModifyEmailAddress(String newAddress, String username) throws Exception {
        Connection connection;
        connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("UPDATE userdetails SET email = ?, emailverified = ?, last_modified_date = ? WHERE username = ?");
        statement.setString(1, newAddress);
        statement.setBoolean(2, true);
        statement.setString(3, date.format(now) + "_01"); // 최종 수정일자 (이메일 주소)
        statement.setString(4, username);
        statement.executeUpdate();
        connection.close();
    }

    public void DeleteUser(String username) throws Exception {
        Connection connection;
        connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("DELETE FROM userdetails WHERE username = ?");
        statement.setString(1, username);
        statement.executeUpdate();
        connection.close();
    }
}
