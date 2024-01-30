package com.example.demo123.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.TransactionManager;

import javax.sql.DataSource;

@Configuration
//@PropertySource("classpath:db.properties")
public class DbConfig extends AbstractJdbcConfiguration {

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
            dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/forspring");
            // todo 깃허브에 업로드하기 전 하드코딩 제거
            dataSource.setUsername("root");
            dataSource.setPassword("park5505");
            return dataSource;
        } catch (Exception e) {
            return null;
        }
    }

    @Bean
    NamedParameterJdbcOperations namedParameterJdbcOperations(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    TransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
