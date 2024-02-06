package com.example.demo123.data.dao;

import com.example.demo123.config.DbConfig;
import com.example.demo123.data.dto.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

public class UserDao {
    public UserDao(){};
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(DbConfig.class);
    DataSource dataSource = ctx.getBean("dataSource", DataSource.class);

    public boolean confirmForUsable(User subscriber) throws Exception {
        Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE user_name = ? AND email = ?");
        statement.setString(1, subscriber.getUser_name());
        statement.setString(2, subscriber.getEmail());
        ResultSet resultSet = statement.executeQuery();
        return !resultSet.next(); // 행이 존재할시 false(!true) 반환
    }

    public HashMap<String, String> signUp(User subscriber) throws Exception {
        HashMap<String, String> map = new HashMap<>();

        Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("INSERT INTO users (user_name, email, password) VALUES (?, ?, ?)");
        statement.setString(1, subscriber.getUser_name());
        statement.setString(2, subscriber.getEmail());
        statement.setString(3, subscriber.getPassword());
        int insertedRow = statement.executeUpdate();
        map.put("INSERTED_rows_number", Integer.valueOf(insertedRow).toString());
        System.out.println("INSERTED_rows_number: " + insertedRow);
        return map;
    }
}
