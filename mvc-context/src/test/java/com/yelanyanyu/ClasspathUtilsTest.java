package com.yelanyanyu;

import com.bean.Class01;
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
        ComponentScan annotation = ClassPathUtils.findAnnotation(Class01.class, ComponentScan.class);
        System.out.println(annotation);
    }
}
