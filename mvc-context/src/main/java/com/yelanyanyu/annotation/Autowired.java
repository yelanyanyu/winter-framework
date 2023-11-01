package com.yelanyanyu.annotation;

import java.lang.annotation.*;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
    boolean value() default true;

    /**
     * bean name
     */
    String name() default "";

}
