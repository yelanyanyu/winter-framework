package com.yelanyanyu;

import com.bean.class01;
import com.yelanyanyu.context.AnnotationConfigApplicationContext;
import org.junit.Test;

import java.util.Set;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class AnnotationConfigApplicationContextTest {
    @Test
    public void t1() {
        AnnotationConfigApplicationContext ioc = new AnnotationConfigApplicationContext(class01.class, null);
        System.out.println(ioc.getBeans().size());
        ioc.getBeans().values().forEach(System.out::println);
    }

    @Test
    public void t2() {
        System.out.println(class01.class.getSimpleName());
    }
}
