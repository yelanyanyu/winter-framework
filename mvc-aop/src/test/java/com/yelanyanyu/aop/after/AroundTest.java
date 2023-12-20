package com.yelanyanyu.aop.after;

import com.yelanyanyu.context.AnnotationConfigApplicationContext;
import com.yelanyanyu.context.ConfigurableApplicationContext;
import com.yelanyanyu.io.PropertyResolver;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Slf4j
public class AroundTest {
    private ConfigurableApplicationContext ioc;
    private ClassLoader classLoader;
    @Before
    public void pre() throws IOException {
        classLoader = this.getClass().getClassLoader();

        Properties properties = new Properties();
        properties.load(classLoader.getResourceAsStream("application.properties"));

        PropertyResolver propertyResolver = new PropertyResolver(properties);
        ioc = new AnnotationConfigApplicationContext(AfterApplication.class, propertyResolver);
    }

    @Test
    public void t1() {
        OriginBean bean = ioc.getBean(OriginBean.class);
        bean.hello();
    }

}
