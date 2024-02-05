package com.example.demo123.controller;

import com.example.demo123.data.dto.Post;

import java.util.Map;

public class setDto {
    public setDto(){}
    public void multipurposeDTO(Map<String, String> params, Post post){
        params.forEach((key, value) -> {
            switch (key) {
                case "writer" ->
                        post.setWriter(value);
                case "email" ->
                        post.setEmail(value);
                case "title" ->
                        post.setTitle(value);
                case "content" ->
                        post.setContent(value);
                case "date" -> // sql 쿼리에서 updated_date 컬럼에 값을 대입할 때에도 사용
                        post.setCreated_date(value);
                default ->
                        System.out.println("unKnown value");
            }
        });
    }
}
