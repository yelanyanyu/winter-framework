package com.yelanyanyu;

import com.bean.Class01;
import com.yelanyanyu.context.AnnotationConfigApplicationContext;
import com.yelanyanyu.io.PropertyResolver;
import com.yelanyanyu.util.ClassPathUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class AnnotationConfigApplicationContextTest {
    @Test
    public void t1() throws IOException {
        Properties properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("a.properties"));
        PropertyResolver propertyResolver = new PropertyResolver(properties);
        AnnotationConfigApplicationContext ioc = new AnnotationConfigApplicationContext(Class01.class, propertyResolver);
    }

    @Test
    public void t2() {
        System.out.println(Class01.class.getSimpleName());
    }

    @Test
    public void t3() {

    }
}