package com.yelanyanyu;

import com.bean.Class01;
import com.yelanyanyu.context.AnnotationConfigApplicationContext;
import com.yelanyanyu.io.PropertyResolver;
import com.yelanyanyu.util.ClassPathUtils;
import com.yelanyanyu.util.YamlUtils;
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
//        properties.putAll(YamlUtils.loadYamlMapAsPlainMap("test.yml"));
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("a.properties"));
        PropertyResolver propertyResolver = new PropertyResolver(properties);
        AnnotationConfigApplicationContext ioc = new AnnotationConfigApplicationContext(Class01.class, propertyResolver);
        System.out.println(ioc.getBean("class01"));

    }

    @Test
    public void t2() {
        System.out.println(Class01.class.getSimpleName());
    }

    @Test
    public void t3() {
        Properties properties = new Properties();
        properties.putAll(YamlUtils.loadYamlMapAsPlainMap("test.yml"));
        properties.stringPropertyNames().forEach(System.out::println);
    }
}
