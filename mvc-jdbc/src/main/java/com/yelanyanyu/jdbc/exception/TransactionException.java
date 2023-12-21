package com.yelanyanyu.jdbc.exception;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class TransactionException extends RuntimeException {
    public TransactionException(Throwable cause) {
        super(cause);
    }
}
