package com.yelanyanyu.exception;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class UnsatisfiedDependencyException extends RuntimeException {
    public UnsatisfiedDependencyException(String message) {
        super(message);
    }
}
