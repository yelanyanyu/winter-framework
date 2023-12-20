package com.yelanyanyu.jdbc.exception;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException() {
    }

}

