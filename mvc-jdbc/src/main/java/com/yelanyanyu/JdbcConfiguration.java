package com.yelanyanyu;

import com.yelanyanyu.annotation.Autowired;
import com.yelanyanyu.annotation.Bean;
import com.yelanyanyu.annotation.Configuration;
import com.yelanyanyu.annotation.Value;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Configuration
public class JdbcConfiguration {
    @Bean(destroyMethod = "close")
    public DataSource dataSource(
            @Value("${winter.datasource.url}") String url,
            @Value("${winter.datasource.username}") String username,
            @Value("${winter.datasource.password}") String password,
            @Value("${winter.datasource.driver-class-name:}") String driverClassName,
            @Value("${winter.datasource.maximum-pool-size:20}") int maxPoolSize,
            @Value("${winter.datasource.minimum-pool-size:1}") int minPoolSize,
            @Value("${winter.datasource.connection-timeout:30000}") int coonTimeout
    ) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setAutoCommit(false);
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        if (driverClassName != null) {
            hikariConfig.setDriverClassName(driverClassName);
        }
        hikariConfig.setMaximumPoolSize(maxPoolSize);
        hikariConfig.setMinimumIdle(minPoolSize);
        hikariConfig.setConnectionTimeout(coonTimeout);

        return new HikariDataSource(hikariConfig);
    }

    @Bean
    JdbcTemplate jdbcTemplate(@Autowired DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }


}
