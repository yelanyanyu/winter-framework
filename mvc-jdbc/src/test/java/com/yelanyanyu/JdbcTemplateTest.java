package com.yelanyanyu;

import com.yelanyanyu.bean.Monster;
import com.yelanyanyu.context.AnnotationConfigApplicationContext;
import com.yelanyanyu.context.ApplicationContext;
import com.yelanyanyu.io.PropertyResolver;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Slf4j
public class JdbcTemplateTest {
    private JdbcTemplate jdbcTemplate;
    private ApplicationContext ioc;
    private ClassLoader classLoader;

    @Before
    public void before() throws IOException {
        classLoader = this.getClass().getClassLoader();

        Properties properties = new Properties();
        properties.load(classLoader.getResourceAsStream("application.properties"));

        PropertyResolver propertyResolver = new PropertyResolver(properties);
        log.info("url: {}", propertyResolver.getProperty("${winter.datasource.url}"));

        ioc = new AnnotationConfigApplicationContext(JdbcConfig.class, propertyResolver);
        jdbcTemplate = ioc.getBean(JdbcTemplate.class);
        Assert.assertNotNull(jdbcTemplate);
    }

    @Test
    public void t1() {
        jdbcTemplate.queryForList("select * from monster", Monster.class).forEach(System.out::println);
        System.out.println("====================================");
        jdbcTemplate.update("insert into monster (id, age, birthday, email, gender, name, salary) values (?,?,?,?,?,?,?)",
                null, 18, "2020-01-01", "123@outlook.com", "1", "五", 10000);
        jdbcTemplate.queryForList("select * from monster", Monster.class).forEach(System.out::println);
    }


    @After
    public void after() {
        ioc.close();
    }
}
