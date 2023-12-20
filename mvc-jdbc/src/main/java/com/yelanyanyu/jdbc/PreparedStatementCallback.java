package com.yelanyanyu.jdbc;

import com.yelanyanyu.jdbc.exception.DataAccessException;
import jakarta.annotation.Nullable;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Do action on PreparedStatement and send something back with type T.
 *
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@FunctionalInterface
public interface PreparedStatementCallback<T> {
    /**
     * @param ps
     * @return
     * @throws SQLException
     * @throws DataAccessException
     */
    @Nullable
    T doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException;
}
