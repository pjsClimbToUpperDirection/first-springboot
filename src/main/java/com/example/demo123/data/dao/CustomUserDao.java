package com.example.demo123.data.dao;

import com.example.demo123.config.DbConfig;
import com.example.demo123.data.dto.CustomUserDetails;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class CustomUserDao {
    public CustomUserDao(){}

    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(DbConfig.class);
    DataSource dataSource = ctx.getBean("dataSource", DataSource.class);

    Date now = new Date();
    SimpleDateFormat date = new SimpleDateFormat("yy-MM-dd-HH-mm"); // string 타입

    public ResultSet findUserDetailsByUserName(String username) throws Exception {
        Connection connection;
        connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM userdetails WHERE username = ?");
        statement.setString(1, username);
        return statement.executeQuery();
    }

    public void createUser(CustomUserDetails customUserDetails) throws Exception {
        Connection connection;
        connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("INSERT INTO userdetails (username, password, email, emailVerified, locked, role, created_date) values (?, ?, ?, ?, ?, ?, ?)");
        statement.setString(1, customUserDetails.getUsername());
        statement.setString(2, customUserDetails.getPassword());
        statement.setString(3, customUserDetails.getEmail());
        statement.setBoolean(4, false); // 계정 생성후 별도로 이메일 인증 가능
        statement.setBoolean(5, false);
        statement.setString(6, "u");
        statement.setString(7, date.format(now));
        statement.executeUpdate();
        connection.close();
    }

    public void verifyEmail(String username) throws Exception {
        Connection connection;
        connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("UPDATE userdetails SET emailVerified = true WHERE username = ?");
        statement.setString(1, username);
        statement.executeUpdate();
        connection.close();
    }

    public void DeleteNotVerifiedAccount() throws Exception {
        Connection connection;
        connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("DELETE FROM userdetails WHERE emailVerified = false");
        statement.executeUpdate();
        connection.close();
    }
}
