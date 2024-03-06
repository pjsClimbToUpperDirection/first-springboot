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

    // todo resultSet 인스턴스 반환하지 않도록 수정하기
    public ResultSet findUserDetailsByUserName(String username) throws Exception { // 해당 메서드 호출 시 반드시 close() 메서드 사용하여 ResultSet 을 닫을 것
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

    public void ModifyEmailAddress(String newAddress, String username) throws Exception {
        Connection connection;
        connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("UPDATE userdetails SET email = ?, emailverified = ? WHERE username = ?");
        statement.setString(1, newAddress);
        statement.setBoolean(2, true);
        statement.setString(3, username);
        statement.executeUpdate();
        connection.close();
    }

    public void ModifyPassword(UserForm userForm) throws Exception {
        Connection connection;
        connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("UPDATE userdetails SET password = ? WHERE username = ?");
        statement.setString(1, userForm.getPassword()); // 변경될 pw
        statement.setString(2, userForm.getUsername());
        statement.executeUpdate();
        connection.close();
    }
}
