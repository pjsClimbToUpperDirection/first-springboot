package com.example.demo123.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
//@PropertySource("classpath:db.properties")
public class DbConfig {

    // todo value 어노테이션을 사용하여 설정파일 변수값을 가져와 구성에서 사용할수 있도록 하기
    ///@Value("db.username")
    private String username;
    //@Value("db.password")
    private String password;

    @Bean
    public DataSource dataSource() {
        try {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver"); // 본 자바 애플리케이션과 MySql 을 연결하는 드라이버 클래스
            dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/techblog");
            // todo 깃허브에 업로드하기 전 하드코딩 제거
            dataSource.setUsername("park");
            dataSource.setPassword("park5505");
            return dataSource;
        } catch (Exception e) {
            return null;
        }
    }
}
