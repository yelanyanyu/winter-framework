package com.yelanyanyu.jdbc;

import cn.hutool.core.bean.BeanUtil;
import com.yelanyanyu.jdbc.exception.DataAccessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to execute SQL statements or queries, encapsulating JDBC operations.
 *
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0.0
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
     * @param action the callback object that specifies the action. Define how to use specific connection.
     * @param <T>    the result type of the callback action
     * @return a result object returned by the action, or {@code null}
     */
    public <T> T execute(ConnectionCallback<T> action) {
        try (Connection connection = dataSource.getConnection()) {
            boolean autoCommit = connection.getAutoCommit();
            if (!autoCommit) {
                connection.setAutoCommit(true);
            }
            // Actually execute the action
            T result = action.doInConnection(connection);
            if (!autoCommit) {
                connection.setAutoCommit(false);
            }
            return result;
        } catch (SQLException e) {
            log.error("exception: ", e);
            throw new DataAccessException("Invalid connection");
        }
    }

    /**
     * Execute a JDBC data access operation, implemented as callback action working on a JDBC PreparedStatement. This method is used to PreparedStatement's creation and use.
     *
     * @param psc    object that can create a PreparedStatement given a Connection
     * @param action callback object that specifies the action. In other words, Define what type of action(update, query and the like) you want to perform on PreparedStatement.
     * @param <T>    the result type of the callback action
     * @return a result object returned by the action, or {@code null}
     */
    public <T> T execute(PreparedStatementCreator psc, PreparedStatementCallback<T> action) {
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
     * Execute update operation with SQL statement and arguments. This method can insert, update or delete data and return the number of rows affected.
     *
     * @param sql  SQL statement
     * @param args arguments
     * @return the number of rows affected
     */
    public int update(String sql, Object... args) {
        // tell executor do update operation
        return execute(preparedStatementCreator(sql, args), PreparedStatement::executeUpdate);
    }

    /**
     * Execute query operation with SQL statement and arguments. This method can query data and return the result.
     *
     * @param sql       .
     * @param rowMapper convert from ResultSet to Java Bean
     * @param args      .
     * @param <T>       .
     * @return .
     */
    public <T> List<T> queryForList(String sql, RowMapper<T> rowMapper, Object... args) {
        return execute(preparedStatementCreator(sql, args), ps -> {
            try (ResultSet rs = ps.executeQuery()) {
                List<T> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(rowMapper.mapRow(rs, rs.getRow()));
                }
                return result;
            }
        });
    }

    /**
     * Execute query ops with SQL, and convert the result to a list of clazz.
     *
     * @param sql   .
     * @param clazz .
     * @param args  .
     * @param <T>   .
     * @return .
     */
    public <T> List<T> queryForList(String sql, Class<T> clazz, Object... args) {
        return queryForList(sql, new BeanRowMapper<>(clazz), args);
    }

    /**
     * Execute query ops with SQL that query for one row one col, and convert the result to single number. Correct SQL is like "select count(*) from table_name".
     *
     * @param sql  .
     * @param args .
     * @return .
     */
    public Number queryForNumber(String sql, Object... args) {
        return execute(preparedStatementCreator(sql, args), ps -> {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return (Number) rs.getObject(1);
                }
                return null;
            }
        });
    }

    /**
     * Execute query ops for only one row, and convert the result to the given clazz. This method only support String, Boolean and Number and Java Bean, If you want to convert to other type like Date.class, please use {@link #queryForObject(String, RowMapper, Object...)}.
     *
     * @param sql   .
     * @param clazz .
     * @param args  .
     * @param <T>   .
     * @return .
     */
    @SuppressWarnings("unchecked")
    public <T> T queryForObject(String sql, Class<T> clazz, Object... args) {
        if (clazz == String.class) {
            return (T) queryForObject(sql, StringRowMapper.INSTANCE, args);
        }

        if (clazz == Boolean.class || clazz == boolean.class) {
            return (T) queryForObject(sql, BooleanRowMapper.INSTANCE, args);
        }

        if (Number.class.isAssignableFrom(clazz) || clazz.isPrimitive()) {
            return (T) queryForObject(sql, NumberRowMapper.INSTANCE, args);
        }

        return queryForObject(sql, new BeanRowMapper<>(clazz), args);
    }

    /**
     * Execute query ops for only one row, and convert the result to the given clazz. This method support any type, but you need to provide a RowMapper to convert ResultSet to the given type.
     *
     * @param sql       .
     * @param rowMapper .
     * @param args      .
     * @param <T>       .
     * @return .
     */
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) {
        return execute(preparedStatementCreator(sql, args), ps -> {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rowMapper.mapRow(rs, rs.getRow());
                }
                return null;
            }
        });
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

    /**
     * Convert ResultSet to String.
     */
    static class StringRowMapper implements RowMapper<String> {
        static StringRowMapper INSTANCE = new StringRowMapper();

        @Override
        public String mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            return resultSet.getString(1);
        }
    }

    /**
     * Convert ResultSet to Boolean.
     */
    static class BooleanRowMapper implements RowMapper<Boolean> {
        static BooleanRowMapper INSTANCE = new BooleanRowMapper();

        @Override
        public Boolean mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            return resultSet.getBoolean(1);
        }
    }

    /**
     * Convert ResultSet to Number.
     */
    static class NumberRowMapper implements RowMapper<Number> {
        static NumberRowMapper INSTANCE = new NumberRowMapper();

        @Override
        public Number mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            return (Number) resultSet.getObject(1);
        }
    }

    /**
     * Convert ResultSet to Java Bean.
     *
     * @param <T> .
     */
    static class BeanRowMapper<T> implements RowMapper<T> {
        private final Class<T> clazz;

        public BeanRowMapper(Class<T> clazz) {
            this.clazz = clazz;
        }

        @Override
        public T mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            T t = BeanUtils.instantiateClass(clazz);
            int columnCount = resultSet.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = resultSet.getMetaData().getColumnName(i);
                Object columnValue = resultSet.getObject(columnName);
                // use cn.hutool.core.bean.BeanUtil to set property
                BeanUtil.setProperty(t, columnName, columnValue);
            }
            return t;
        }
    }

}
