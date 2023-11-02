package com.yelanyanyu.exception;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class NoSuchBeanDefinitionException extends RuntimeException{
    public NoSuchBeanDefinitionException(String message) {
        super(message);
    }
}
