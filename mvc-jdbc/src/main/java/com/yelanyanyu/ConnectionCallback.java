package com.yelanyanyu;

import jakarta.annotation.Nullable;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Callback interface for JDBC code. To be used with JdbcTemplate's execution methods, often as anonymous classes
 *
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public interface ConnectionCallback<T> {
    /**
     * Do any operations on the connection
     *
     * @param connection the connection
     * @return the result of the operation
     * @throws SQLException if a database access error occurs
     */
    @Nullable
    T doInConnection(Connection connection) throws SQLException;
}
