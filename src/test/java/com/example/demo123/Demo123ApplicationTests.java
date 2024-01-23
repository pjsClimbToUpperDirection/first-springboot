package com.example.demo123;

import com.example.demo123.data.entity.PostEntity;
import com.example.demo123.data.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
class Demo123ApplicationTests {

	@Autowired
	public PostRepository postRepository;
	@Test
	@Transactional
	void testJpa() {
		PostEntity ps = new PostEntity();
		ps.setContent("??");
		ps.setEmail("dd");
		this.postRepository.save(ps); // entity를 인자로 받음
	}
}
