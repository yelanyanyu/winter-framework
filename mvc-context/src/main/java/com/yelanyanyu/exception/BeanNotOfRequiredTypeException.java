package com.yelanyanyu.exception;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class BeanNotOfRequiredTypeException extends RuntimeException {
    public BeanNotOfRequiredTypeException(String message) {
        super(message);
    }
}
