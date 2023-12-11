package com.yelanyanyu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Callback interface for JDBC code. To be used with JdbcTemplate's execution methods, often as anonymous classes
 *
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@FunctionalInterface
public interface PreparedStatementCreator {
    /**
     * Create a statement in this connection. Allows implementations to use PreparedStatements. The JdbcTemplate will close the created statement.
     *
     * @param connection the connection used to create statement
     * @return a prepared statement
     * @throws SQLException if a database access error occurs
     */
    PreparedStatement createPreparedStatement(Connection connection) throws SQLException;
}
