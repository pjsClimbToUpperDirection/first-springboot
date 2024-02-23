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
public class TokenDao {
    public TokenDao(){}
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(DbConfig.class);
    DataSource dataSource = ctx.getBean("dataSource", DataSource.class);

    public void InsertRefreshToken(String refreshToken) throws SQLException {
        Connection connection;
        connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("INSERT INTO refreshtokenlist (refreshtoken) VALUES (?)"); // todo issuerlist
        statement.setString(1, refreshToken);
        statement.executeUpdate();
        connection.close();
    }

    public String findRefreshToken(String refreshToken) throws Exception {
        Connection connection;
        connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM refreshtokenlist WHERE refreshtoken = ?");
        statement.setString(1, refreshToken);
        String selected =  statement.executeQuery().getString(1);
        connection.close();
        return selected;
    }
}
