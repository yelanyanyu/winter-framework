package com.yelanyanyu;

import com.yelanyanyu.exception.DataAccessException;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This class is used to execute SQL statements or queries, encapsulating JDBC operations.
 *
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Slf4j
public class JdbcTemplate {
    DataSource dataSource;

    public JdbcTemplate(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Execute a JDBC data access operation, implemented as callback action working on a JDBC Connection.
     *
     * @param action the callback object that specifies the action
     * @param <T>    the result type of the callback action
     * @return a result object returned by the action, or {@code null}
     */
    public <T> T execute(ConnectionCallback<T> action) {
        // TODO: wait for implementation
        try (Connection connection = dataSource.getConnection()) {
            T result = action.doInConnection(connection);
            return result;
        } catch (SQLException e) {
            log.error("exception: ", e);
            throw new DataAccessException("Invalid connection");
        }
    }

    /**
     * Execute a JDBC data access operation, implemented as callback action working on a JDBC PreparedStatement.
     *
     * @param psc    object that can create a PreparedStatement given a Connection
     * @param action callback object that specifies the action
     * @param <T>    the result type of the callback action
     * @return a result object returned by the action, or {@code null}
     */
    public <T> T execute(PreparedStatementCreator psc, PreparedStatementCallback<T> action) {
        // TODO: wait for implementation
        return execute(connection -> {
            try (PreparedStatement ps = psc.createPreparedStatement(connection)) {
                T result = action.doInPreparedStatement(ps);
                return result;
            } catch (SQLException e) {
                log.error("exception: ", e);
                throw new DataAccessException("Invalid statement");
            }
        });
    }

    /**
     * Execute update operation with SQL statement and arguments.
     *
     * @param sql  SQL statement
     * @param args arguments
     * @return the number of rows affected
     */
    public int update(String sql, Object... args) {
        return execute(preparedStatementCreator(sql, args), PreparedStatement::executeUpdate);
    }

    /**
     * bind logic to PreparedStatement creation
     *
     * @param sql  SQL statement
     * @param args arguments
     * @return PreparedStatementCreator
     */
    private PreparedStatementCreator preparedStatementCreator(String sql, Object... args) {
        return (connection) -> {
            // create PreparedStatement
            PreparedStatement ps = connection.prepareStatement(sql);
            // bind args to PreparedStatement
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]); // JDBC PreparedStatement index starts from 1
            }
            return ps;
        };
    }

}
