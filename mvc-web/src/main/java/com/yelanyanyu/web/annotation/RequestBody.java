package com.yelanyanyu.web.annotation;

import java.lang.annotation.*;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestBody {
}
