package com.example.demo123.data.dao;

import com.example.demo123.component.Translater;
import com.example.demo123.config.DbConfig;
import com.example.demo123.data.dto.Post;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;


// 추후 반복되는 코드를 분리할수 있도록 한다.
// 정의된 다음 네 가지 메서드 이외에 추가 메서드 작성은 지양할 것
@Repository
public class PostDao {
    private final Translater translater;
    public PostDao(Translater translater){
        this.translater = translater;
    }
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(DbConfig.class);
    DataSource dataSource = ctx.getBean("dataSource", DataSource.class);

    Date now = new Date();
    SimpleDateFormat date = new SimpleDateFormat("yy-MM-dd"); // string 타입

    public void InsertPost(Post post) throws Exception{
        Connection connection;
        connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("INSERT INTO posts (writer, title, content, created_date) VALUES (?, ?, ?, ?)");
        statement.setString(1, post.getWriter());
        statement.setString(2, post.getTitle());
        statement.setString(3, post.getContent());
        statement.setString(4, date.format(now));
        int insertedRow = statement.executeUpdate();

        System.out.println("INSERTED_rows_number: " + insertedRow);
        connection.close();
    }

    // 조회 영역이므로 유일하게 본문을 반환
    public ArrayList<Post> lookUpPosts(Post post) throws Exception {
        ArrayList<Post> postArrayList;
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
        postArrayList = translater.ForResultSet(selectedRow);
        connection.close();
        return postArrayList;
    }


    // 제목은 수정할수 없도록 함, 업로드 시 작성한 제목(unique)을 사용하여 기존 게시글 조회, 갱신
    public void updatePost(Post newOne) throws Exception {
        Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("UPDATE posts SET content = ?, updated_date = ? WHERE writer = ? AND title = ?");
        statement.setString(1, newOne.getContent());
        statement.setString(2, date.format(now));
        statement.setString(3, newOne.getWriter());
        statement.setString(4, newOne.getTitle());
        int updatedRow = statement.executeUpdate();

        System.out.println("UPDATED_rows_number: " + updatedRow);
        connection.close();
    }
    public void DeletePost(Post post) throws Exception {
        Connection connection = dataSource.getConnection();
        String defaultStatement = "DELETE FROM posts WHERE ";
        String StatementAsResult;
        StatementAsResult = defaultStatement + "writer = ? AND title = ?";
        PreparedStatement statement = connection.prepareStatement(StatementAsResult);
        statement.setString(1, post.getWriter());
        statement.setString(2, post.getTitle());

        int deletedRow = statement.executeUpdate();
        System.out.println("DELETED_rows_number: " + deletedRow);
        connection.close();
    }

    public void DeleteAllPosts(String username) throws Exception {
        Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("DELETE FROM posts WHERE writer = ?");
        statement.setString(1, username);

        int deletedRow = statement.executeUpdate();
        System.out.println("DELETED_rows_number: " + deletedRow);
        connection.close();
    }
}
