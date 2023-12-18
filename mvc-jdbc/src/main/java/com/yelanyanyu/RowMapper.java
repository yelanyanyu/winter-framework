package com.yelanyanyu;

import jakarta.annotation.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Callback interface for JDBC code. To be used with JdbcTemplate's execution methods, often as anonymous classes
 *
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@FunctionalInterface
public interface RowMapper<T> {
    /**
     * Map the result set to any Java Bean in custom way. One row one object.
     *
     * @param resultSet the ResultSet to map
     * @param rowNum    the number of the current row
     * @return the result object for the current row
     * @throws SQLException if a SQLException is encountered getting column values
     */
    @Nullable
    T mapRow(ResultSet resultSet, int rowNum) throws SQLException;
}
