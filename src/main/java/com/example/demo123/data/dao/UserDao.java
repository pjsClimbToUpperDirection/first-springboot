package com.example.demo123.data.dao;

import com.example.demo123.config.DbConfig;
import com.example.demo123.data.dto.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDao {
    public UserDao(){}
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

    public void signUp(User subscriber) throws Exception {
        Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("INSERT INTO users (user_name, email, password) VALUES (?, ?, ?)");
        statement.setString(1, subscriber.getUser_name());
        statement.setString(2, subscriber.getEmail());
        statement.setString(3, subscriber.getPassword());
        int insertedRow = statement.executeUpdate();
        System.out.println("INSERTED_rows_number: " + insertedRow);
    }

    public void ChangeUserInfo(User changed) throws Exception {
        // 변경하고자 하는 사용자의 user_name, 변경하고자 하는 email, password 값을 dto 로 전송
        Connection connection = dataSource.getConnection();
        String Statement;
        String wantToChange;
        PreparedStatement statement;
        if (changed.getEmail() != null & changed.getPassword() != null) {
            wantToChange = "email = ?, password = ?";
            Statement = "UPDATE users SET " + wantToChange + " WHERE user_name = ?";
            statement = connection.prepareStatement(Statement);
            statement.setString(1, changed.getEmail());
            statement.setString(2, changed.getPassword());
            statement.setString(3, changed.getUser_name());
        } else if (changed.getEmail() != null) {
            wantToChange = "email = ?";
            Statement = "UPDATE users SET " + wantToChange + " WHERE user_name = ?";
            statement = connection.prepareStatement(Statement);
            statement.setString(1, changed.getEmail());
            statement.setString(2, changed.getUser_name());
        } else if (changed.getPassword() != null) {
            wantToChange = "password = ?";
            Statement = "UPDATE users SET " + wantToChange + " WHERE user_name = ?";
            statement = connection.prepareStatement(Statement);
            statement.setString(1, changed.getPassword());
            statement.setString(2, changed.getUser_name());
        } else
            throw new IllegalArgumentException("assign some argument!");

        int changedRow = statement.executeUpdate();
        System.out.println("DELETED_rows_number: " + changedRow);
    }

    public void cancellation(User reSigner) throws Exception {
        Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE user_name = ?");
        statement.setString(1, reSigner.getUser_name());
        int insertedRow = statement.executeUpdate();
        System.out.println("DELETED_rows_number: " + insertedRow);
    }
}
