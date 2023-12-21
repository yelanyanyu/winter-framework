package com.yelanyanyu.jdbc.annotation;

import java.lang.annotation.*;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Transactional {
    String value() default "platformTransactionManager";
}
