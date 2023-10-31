package com.yelanyanyu.annotation;

import jakarta.annotation.Resource;

import java.lang.annotation.*;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ComponentScan
public @interface Component {
    /**
     *
     * @return bean name in context
     */
    String value() default "";
}
