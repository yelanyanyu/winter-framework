package com.yelanyanyu;

import com.yelanyanyu.io.PropertyResolver;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class PropertyResolverTest {
    @Test
    public void t1() throws IOException {
        Properties properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("a.properties"));
        int property = new PropertyResolver(properties).getProperty("${abc:${bcd:${cde:234235}}}", int.class);
        System.out.println(property);
    }

    @Test
    public void t2() {
//        PropertyExpr propertyExpr = PropertyResolver.parsePropertyExpr("${a.b.c:123}");
//        System.out.println(propertyExpr);
    }
}
