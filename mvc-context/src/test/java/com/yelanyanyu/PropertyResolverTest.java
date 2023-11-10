package com.yelanyanyu;

import com.yelanyanyu.io.PropertyResolver;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Slf4j
public class PropertyResolverTest {
    @Test
    public void t1() throws IOException {
        Properties properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("application.yml"));
        log.debug("properties: {}", properties);
        String property = new PropertyResolver(properties).getProperty("${winter.aaa.bbb}");
        log.debug("prop: {}", property);
    }

    @Test
    public void t2() {
//        PropertyExpr propertyExpr = PropertyResolver.parsePropertyExpr("${a.b.c:123}");
//        System.out.println(propertyExpr);
    }

    @Test
    public void t3() {
        PropertyResolver propertyResolver = testUtils.createPropertyResolver();
        String p = propertyResolver.getProperty("${winter.webmvc.resource-path:static/}");
        log.debug("p: {}", p);
    }
}
