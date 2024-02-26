package com.example.demo123.component;

import com.example.demo123.data.dto.Post;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.ArrayList;

@Component
public class Translater {
    public Translater(){}

    public ArrayList<Post> ForResultSet(ResultSet selectedRow) throws Exception{
        ArrayList<Post> result = new ArrayList<>();
        while (selectedRow.next()){
            Post post = new Post();
            post.setPost_id(selectedRow.getInt("post_id"));
            post.setWriter(selectedRow.getString("writer"));
            post.setEmail(selectedRow.getString("email"));
            post.setTitle(selectedRow.getString("title"));
            post.setContent(selectedRow.getString("content"));
            post.setCreated_date(selectedRow.getString("created_date"));
            post.setUpdated_date(selectedRow.getString("updated_date"));
            result.add(post);
        }
        System.out.println("SELECTED_ROWS -> " + result);
        return result;
    }
}
