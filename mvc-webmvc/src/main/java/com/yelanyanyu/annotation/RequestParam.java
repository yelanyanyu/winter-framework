package com.yelanyanyu.annotation;

import com.yelanyanyu.web.bean.ValueConstants;

import java.lang.annotation.*;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {
    String value();
    String defaultValue() default ValueConstants.DEFAULT_NONE;
}
