package com.example.demo123.data.dao;

import com.example.demo123.config.DbConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class LoginStatusDao {
    public LoginStatusDao(){}
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(DbConfig.class);
    DataSource dataSource = ctx.getBean("dataSource", DataSource.class);

    public void activateLoginStatus(String username ,String refreshToken) throws SQLException {
        Connection connection;
        connection = dataSource.getConnection();
        // 리프래시 토큰 저장 전 기존 로그인 정보(리프래시) 삭제
        PreparedStatement statement1 = connection.prepareStatement("DELETE FROM refreshtokenlist where username = ?");
        statement1.setString(1, username);
        statement1.executeUpdate();
        PreparedStatement statement2 = connection.prepareStatement("INSERT INTO refreshtokenlist (username, refresh) VALUES (?, ?)");
        statement2.setString(1, username);
        statement2.setString(2, refreshToken);
        statement2.executeUpdate();
        connection.close();
    }
    public Boolean checkLoginStatus(String refreshToken) throws SQLException {
        Connection connection;
        connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM refreshtokenlist WHERE refresh = ?");
        statement.setString(1, refreshToken);
        ResultSet result = statement.executeQuery();
        return result.next();
    }
}
