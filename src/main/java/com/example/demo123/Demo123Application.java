package com.example.demo123;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing // JPA Auditing(감시, 감사) 기능을 활성화하기 위한 에너테이션, Spring Data 에서 제공
@SpringBootApplication
public class Demo123Application {

	public static void main(String[] args) {
		try {
			SpringApplication.run(Demo123Application.class, args);
		} catch (NullPointerException e) {
			System.err.println("NullPointerException is occurred check your DB: " + e);
		}
	}
}
