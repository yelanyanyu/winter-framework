package com.yelanyanyu.webmvc.exception;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class DuplicateBootConfigException extends RuntimeException{
    public DuplicateBootConfigException(String message) {
        super(message);
    }
}
