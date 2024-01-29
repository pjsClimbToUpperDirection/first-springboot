package com.example.demo123;

import com.example.demo123.controller.DeleteController;
import com.example.demo123.controller.PostController;
import com.example.demo123.data.entity.PostEntity;
import com.example.demo123.data.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;


@SpringBootTest
class Demo123ApplicationTests {

	@Autowired
	public PostRepository postRepository;
	@Test
	@Transactional
	void Save() {
		Map<String, String> params = new HashMap<>();
		params.put("writer", "us");
		params.put("content", "some");
		params.put("email", "4.4@naver.com");
		params.put("title", "TTl");
		new PostController(postRepository).UploadPost(params);
	}

	@Test
	@Transactional
	void Delete() {
		PostEntity ps = new PostEntity();
		new DeleteController(postRepository).DeletePost("22", "");
	}
}
