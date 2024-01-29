package com.example.demo123.db;

import com.example.demo123.config.DbConfig;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


public class dbConfigTest {

    private static Logger logger = LoggerFactory.getLogger(dbConfigTest.class);

    @Test
    public void test14() throws SQLException {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(DbConfig.class);

        DataSource dataSource = ctx.getBean("dataSource22", DataSource.class);
        Assert.notNull(dataSource, "dataSource must not be null");
        testDataSource(dataSource);
    }

    private void testDataSource(DataSource dataSource) throws SQLException {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            Assert.notNull(connection, "connection must not be null");
        } catch (Exception e) {
            System.out.println("unExpected Error");
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
}
