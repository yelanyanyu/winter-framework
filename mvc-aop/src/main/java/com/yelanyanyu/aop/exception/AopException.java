package com.yelanyanyu.aop.exception;

import com.yelanyanyu.exception.NoSuchBeanDefinitionException;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class AopException extends NoSuchBeanDefinitionException {
    public AopException(String message) {
        super(message);
    }
}
