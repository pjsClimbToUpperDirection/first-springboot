package com.example.demo123.data.dao;

import com.example.demo123.config.DbConfig;
import com.example.demo123.data.dto.Post;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

public class PostDao {
    public ResponseEntity<HashMap> InsertPost(Post post, HttpHeaders headers) throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(DbConfig.class);
        DataSource dataSource = ctx.getBean("dataSource", DataSource.class);
        Connection connection = null;
        HashMap<String, String> map = new HashMap<>();
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO posts (writer, email, title, content, img) VALUES (?, ?, ?, ?, ?)");
            statement.setString(1, post.getWriter());
            statement.setString(2, post.getEmail());
            statement.setString(3, post.getTitle());
            statement.setString(4, post.getContent());
            statement.setString(5, post.getImg());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error is occurred on the SQL DB - Insert", e);
        } finally {
            if (connection != null) {
                connection.close();
                map.put("writer", post.getWriter());
                map.put("title", post.getTitle());
                map.put("content", post.getContent());
                map.put("img", post.getImg());
                map.put("email", post.getEmail());
            }
        }
        return new ResponseEntity<>(map, headers, 201);
    }

    public ResponseEntity<HashMap> DeletePost(Post post, HttpHeaders headers) throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(DbConfig.class);
        DataSource dataSource = ctx.getBean("dataSource22", DataSource.class);
        Connection connection = null;
        HashMap<String, String> map = new HashMap<>();
        try {
            connection = dataSource.getConnection();
            // ? 구문으로 선언문에 변수 체크포인트 지정, 1부터 시작하는 인덱스를 통하여 값을 할당
            PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE writer = ? AND title = ?");
            statement.setString(1, post.getWriter());
            statement.setString(2, post.getTitle());
            statement.executeQuery();
        } catch (SQLException e) {
            throw new Exception("Error is occurred on the SQL DB - Delete", e);
        } finally {
            if (connection != null) {
                connection.close();
                map.put("writer", post.getWriter());
                map.put("title", post.getTitle());
            }
        }
        return new ResponseEntity<>(map, headers, 201);
    }
}
