package com.yelanyanyu.web.exception;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class NestedRuntimeException extends RuntimeException {
    public NestedRuntimeException(String message) {
        super(message);
    }
}
