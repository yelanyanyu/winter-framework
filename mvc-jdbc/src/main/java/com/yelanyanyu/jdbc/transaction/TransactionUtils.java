package com.yelanyanyu.jdbc.transaction;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Slf4j
public class TransactionUtils {
    public static Connection getCurrentConnection() {
        TransactionStatus ts = DataSourceTransactionManager.transactionStatus.get();
        return ts == null ? null : ts.connection;
    }
}
