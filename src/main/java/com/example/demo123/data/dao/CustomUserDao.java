package com.example.demo123.data.dao;

import com.example.demo123.config.DbConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class CustomUserDao {
    public CustomUserDao(){};

    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(DbConfig.class);
    DataSource dataSource = ctx.getBean("dataSource", DataSource.class);

    public ResultSet findUserDetailsByUserName(String username) throws Exception {
        Connection connection;
        connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM userdetails WHERE username = ?");
        statement.setString(1, username);
        return statement.executeQuery();
    }
}
