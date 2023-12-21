package com.yelanyanyu.jdbc.transaction;

import java.sql.Connection;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class TransactionStatus {
    final Connection connection;

    public TransactionStatus(Connection connection) {
        this.connection = connection;
    }
}
