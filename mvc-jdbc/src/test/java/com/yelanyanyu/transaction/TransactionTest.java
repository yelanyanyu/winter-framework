package com.yelanyanyu.transaction;

import com.yelanyanyu.JdbcConfig;
import com.yelanyanyu.context.AnnotationConfigApplicationContext;
import com.yelanyanyu.context.ApplicationContext;
import com.yelanyanyu.io.PropertyResolver;
import com.yelanyanyu.jdbc.JdbcTemplate;
import lombok.extern.slf4j.Slf4j;
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
public class TransactionTest {
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
        MonsterService monsterService = ioc.getBean(MonsterService.class);
        monsterService.doAll();
    }
}
