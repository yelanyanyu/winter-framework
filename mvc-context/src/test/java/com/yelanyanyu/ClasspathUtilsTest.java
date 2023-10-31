package com.yelanyanyu;

import com.bean.class01;
import com.yelanyanyu.annotation.Component;
import com.yelanyanyu.annotation.ComponentScan;
import com.yelanyanyu.util.ClassPathUtils;
import org.junit.Test;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class ClasspathUtilsTest {
    @Test
    public void t1() {
        ComponentScan annotation = ClassPathUtils.findAnnotation(class01.class, ComponentScan.class);
        System.out.println(annotation);
    }
}
