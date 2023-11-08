package com.yelanyanyu.annotation;

import com.yelanyanyu.annotation.Component;

import javax.management.StringValueExp;
import java.lang.annotation.*;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Controller {
    String value() default "";
}
