package com.example.demo123.data.dao;

import com.example.demo123.config.DbConfig;
import com.example.demo123.data.dto.Post;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;


// 추후 반복되는 코드를 분리할수 있도록 한다.
// 정의된 다음 네 가지 메서드 이외에 추가 메서드 작성은 지양할 것
public class PostDao {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(DbConfig.class);
    DataSource dataSource = ctx.getBean("dataSource", DataSource.class);



    public ResponseEntity<HashMap> InsertPost(Post post, HttpHeaders httpHeaders) throws Exception{
        Date now = new Date();
        SimpleDateFormat date = new SimpleDateFormat("yy-MM-dd"); // string 타입

        HashMap<String, Integer> map = new HashMap<>();
        int insertedRow;

        Connection connection;
        connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("INSERT INTO posts (writer, email, title, content, created_date) VALUES (?, ?, ?, ?, ?)");
        statement.setString(1, post.getWriter());
        statement.setString(2, post.getEmail());
        statement.setString(3, post.getTitle());
        statement.setString(4, post.getContent());
        statement.setString(5, date.format(now));
        insertedRow = statement.executeUpdate();

        map.put("INSERTED_rows_number", insertedRow);
        System.out.println("INSERTED_rows_number: " + insertedRow);
        connection.close();

        return new ResponseEntity<>(map, httpHeaders, 201);
    }
    public ResponseEntity<HashMap> lookUpPosts(Post post, HttpHeaders httpHeaders) throws Exception {
        Connection connection;

        HashMap<Integer, HashMap<String, String>> map = new HashMap<>();
        ResultSet selectedRow;

        PreparedStatement statement;
        connection = dataSource.getConnection();

        if (post.getWriter() != null & post.getTitle() != null){
            if (post.getCreated_date() != null) {
                statement = connection.prepareStatement("SELECT * FROM posts WHERE writer = ? AND title = ? AND ((created_date = ? AND updated_date IS NULL) OR updated_date = ?)");
                statement.setString(1, post.getWriter());
                statement.setString(2, post.getTitle());
                statement.setString(3, post.getCreated_date());
                statement.setString(4, post.getCreated_date());
            }
            else {
                statement = connection.prepareStatement("SELECT * FROM posts WHERE writer = ? AND title = ?");
                statement.setString(1, post.getWriter());
                statement.setString(2, post.getTitle());
            }
        }
        else if (post.getWriter() != null) {
            if (post.getCreated_date() != null) {
                statement = connection.prepareStatement("SELECT * FROM posts WHERE writer = ? AND ((created_date = ? AND updated_date IS NULL) OR updated_date = ?)");
                statement.setString(1, post.getWriter());
                statement.setString(2, post.getCreated_date());
                statement.setString(3, post.getCreated_date());
            }
            else {
                statement = connection.prepareStatement("SELECT * FROM posts WHERE writer = ?");
                statement.setString(1, post.getWriter());
            }
        }
        else if (post.getTitle() != null){
            if (post.getCreated_date() != null) {
                statement = connection.prepareStatement("SELECT * FROM posts WHERE title = ? AND ((created_date = ? AND updated_date IS NULL) OR updated_date = ?)");
                statement.setString(1, post.getTitle());
                statement.setString(2, post.getCreated_date());
                statement.setString(3, post.getCreated_date());
            }
            else {
                statement = connection.prepareStatement("SELECT * FROM posts WHERE title = ?");
                statement.setString(1, post.getTitle());
            }
        }
        else {
            statement = connection.prepareStatement("SELECT * FROM posts WHERE (created_date = ? AND updated_date IS NULL) OR updated_date = ?");
            statement.setString(1, post.getCreated_date());
            statement.setString(2, post.getCreated_date());
        }
        selectedRow = statement.executeQuery();

        resultSet(selectedRow, map);

        connection.close();

        return new ResponseEntity<>(map, httpHeaders, 201);
    }
    public ResponseEntity<HashMap> updatePosts(Post selected, Post updated, HttpHeaders httpHeaders) throws Exception{
        Date now = new Date();
        SimpleDateFormat date = new SimpleDateFormat("yy-MM-dd"); // string 타입

        Connection connection;

        HashMap<String, Integer> map = new HashMap<>();
        int UpdatedRow;

        connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("UPDATE posts SET title = ?, content = ?, updated_date = ? WHERE writer = ? AND title = ?");
        statement.setString(1, updated.getTitle());
        statement.setString(2, updated.getContent());
        statement.setString(3, date.format(now));
        statement.setString(4, selected.getWriter());
        statement.setString(5, selected.getTitle());
        UpdatedRow = statement.executeUpdate();

        map.put("UPDATED_rows_number", UpdatedRow);
        System.out.println("UPDATED_rows_number: " + UpdatedRow);
        connection.close();

        return new ResponseEntity<>(map, httpHeaders, 201);
    }
    public ResponseEntity<HashMap> DeletePost(Post post, HttpHeaders httpHeaders) throws Exception {
        Connection connection;

        HashMap<String, Integer> map = new HashMap<>();
        int DeletedRow;

        PreparedStatement statement;
        connection = dataSource.getConnection();
        if(post.getCreated_date() == null) {
            statement = connection.prepareStatement("DELETE FROM posts WHERE writer = ? AND title = ?");
            statement.setString(1, post.getWriter());
            statement.setString(2, post.getTitle());
        }
        else {
            statement = connection.prepareStatement("DELETE FROM posts WHERE writer = ? AND title = ? AND ((created_date = ? AND updated_date IS NULL) OR updated_date = ?)");
            statement.setString(1, post.getWriter());
            statement.setString(2, post.getTitle());
            statement.setString(3, post.getCreated_date());
            statement.setString(4, post.getCreated_date());
        }
        DeletedRow = statement.executeUpdate();

        map.put("DELETED_rows_number", DeletedRow);
        System.out.println("DELETED_rows_number: " + DeletedRow);
        connection.close();

        return new ResponseEntity<>(map, httpHeaders, 201);
    }



    private void resultSet(ResultSet selectedRow, HashMap<Integer, HashMap<String, String>> map) throws Exception{
        String[] columns = {"post_id", "writer", "email", "title", "content", "created_date", "updated_date"};
        while (selectedRow.next()){
            HashMap<String, String> columnList = new HashMap<>();
            for (String column : columns) {
                columnList.put(column, selectedRow.getString(column));
            }
            map.put(selectedRow.getInt("post_id"), columnList);
        }
        System.out.println("SELECTED_ROWS -> " + map);
    }
}
