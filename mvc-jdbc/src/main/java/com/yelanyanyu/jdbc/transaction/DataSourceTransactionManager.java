package com.yelanyanyu.jdbc.transaction;

import com.yelanyanyu.annotation.Component;
import com.yelanyanyu.jdbc.exception.TransactionException;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Slf4j
@Component
public class DataSourceTransactionManager implements PlatformTransactionManager, InvocationHandler {
    static final ThreadLocal<TransactionStatus> transactionStatus = new ThreadLocal<>();
    final DataSource dataSource;

    public DataSourceTransactionManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // TODO: Implement the transaction.
        TransactionStatus ts = transactionStatus.get();
        if (ts == null) {
            // If there is no transaction exists, then create one and update the status.
            try (Connection connection = dataSource.getConnection()) {
                // Close auto commit, give the control to transaction.
                boolean autoCommit = connection.getAutoCommit();
                if (autoCommit) {
                    connection.setAutoCommit(false);
                }

                try {
                    // Start transaction.

                    // Set the status of threadLocal.
                    transactionStatus.set(new TransactionStatus(connection));
                    // invoke the target method.
                    Object ret = method.invoke(proxy, args);
                    // Commit transaction.
                    connection.commit();

                    return ret;
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException |
                         SQLException e) {
                    TransactionException te = new TransactionException(e.getCause());
                    try {
                        connection.rollback();
                    } catch (SQLException ex) {
                        log.error("Rollback failed.", ex);
                        te.addSuppressed(ex);
                    }
                    log.error("Transaction failed.", e);
                    throw te;
                } finally {
                    transactionStatus.remove();
                    // Rollback the auto commit status.
                    if (autoCommit) {
                        connection.setAutoCommit(true);
                    }
                }

            }
        }
        // If there is a transaction working, then add this to the transaction.
        return method.invoke(proxy, args);
    }
}
