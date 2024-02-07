package com.example.demo123.data.dao;

import com.example.demo123.config.DbConfig;
import com.example.demo123.data.dto.Post;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;


// 추후 반복되는 코드를 분리할수 있도록 한다.
// 정의된 다음 네 가지 메서드 이외에 추가 메서드 작성은 지양할 것
public class PostDao {
    public PostDao(){}
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(DbConfig.class);
    DataSource dataSource = ctx.getBean("dataSource", DataSource.class);

    Date now = new Date();
    SimpleDateFormat date = new SimpleDateFormat("yy-MM-dd"); // string 타입

    public void InsertPost(Post post) throws Exception{
        Connection connection;
        connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("INSERT INTO posts (writer, email, title, content, created_date) VALUES (?, ?, ?, ?, ?)");
        statement.setString(1, post.getWriter());
        statement.setString(2, post.getEmail());
        statement.setString(3, post.getTitle());
        statement.setString(4, post.getContent());
        statement.setString(5, date.format(now));
        int insertedRow = statement.executeUpdate();

        System.out.println("INSERTED_rows_number: " + insertedRow);
        connection.close();
    }

    // 조회 영역이므로 유일하게 본문을 반환
    public ArrayList<HashMap<String, String>> lookUpPosts(Post post) throws Exception {
        Connection connection;
        connection = dataSource.getConnection();
        PreparedStatement statement;

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

        ResultSet selectedRow = statement.executeQuery();
        ArrayList<HashMap<String, String>> selectedList = resultSet(selectedRow);
        connection.close();

        return selectedList;
    }


    public void updatePosts(Post selected, Post updated) throws Exception{
        Connection connection;
        connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("UPDATE posts SET title = ?, content = ?, updated_date = ? WHERE writer = ? AND title = ?");
        statement.setString(1, updated.getTitle());
        statement.setString(2, updated.getContent());
        statement.setString(3, date.format(now));
        statement.setString(4, selected.getWriter());
        statement.setString(5, selected.getTitle());
        int updatedRow = statement.executeUpdate();

        System.out.println("UPDATED_rows_number: " + updatedRow);
        connection.close();
    }
    public void DeletePost(Post post) throws Exception {
        PreparedStatement statement;
        Connection connection = dataSource.getConnection();
        String defaultStatement = "DELETE FROM posts WHERE ";
        String StatementAsResult;
        if (post.getWriter() != null & post.getTitle() != null) {
            if (post.getCreated_date() != null) {
                StatementAsResult = defaultStatement + "writer = ? AND title = ? AND ((created_date = ? AND updated_date IS NULL) OR updated_date = ?)";
                statement = connection.prepareStatement(StatementAsResult);
                statement.setString(1, post.getWriter());
                statement.setString(2, post.getTitle());
                statement.setString(3, post.getCreated_date());
                statement.setString(4, post.getCreated_date());
            } else {
                StatementAsResult = defaultStatement + "writer = ? AND title = ?";
                statement = connection.prepareStatement(StatementAsResult);
                statement.setString(1, post.getWriter());
                statement.setString(2, post.getTitle());
            }
        } else if (post.getWriter() != null) {
            if (post.getCreated_date() != null) {
                StatementAsResult = defaultStatement + "writer = ? AND ((created_date = ? AND updated_date IS NULL) OR updated_date = ?)";
                statement = connection.prepareStatement(StatementAsResult);
                statement.setString(1, post.getWriter());
                statement.setString(2, post.getCreated_date());
                statement.setString(3, post.getCreated_date());
            } else
                throw new IllegalArgumentException("argument must be two more");
        } else if (post.getTitle() != null) {
            if (post.getCreated_date() != null) {
                StatementAsResult = defaultStatement + "title = ? AND ((created_date = ? AND updated_date IS NULL) OR updated_date = ?)";
                statement = connection.prepareStatement(StatementAsResult);
                statement.setString(1, post.getTitle());
                statement.setString(2, post.getCreated_date());
                statement.setString(3, post.getCreated_date());
            } else
                throw new IllegalArgumentException("argument must be two more");
        } else
            throw new Exception("failed at construction of the SQL Query");


        int deletedRow = statement.executeUpdate();
        System.out.println("DELETED_rows_number: " + deletedRow);
        connection.close();
    }



    private ArrayList<HashMap<String, String>> resultSet(ResultSet selectedRow) throws Exception{
        String[] columns = {"post_id", "writer", "email", "title", "content", "created_date", "updated_date"};
        ArrayList<HashMap<String, String>> rowList = new ArrayList<>();
        while (selectedRow.next()){
            HashMap<String, String> columnList = new HashMap<>();
            for (String column : columns) {
                columnList.put(column, selectedRow.getString(column));
            }
            rowList.add(columnList);
        }
        System.out.println("SELECTED_ROWS -> " + rowList);
        return rowList;
    }
}
