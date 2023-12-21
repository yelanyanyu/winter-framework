package com.yelanyanyu.web.annotation;

import java.lang.annotation.*;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GetMapping {
    String value();
}
