package com.yelanyanyu;

import com.yelanyanyu.annotation.ComponentScan;
import com.yelanyanyu.annotation.Import;
import com.yelanyanyu.annotation.Order;
import com.yelanyanyu.jdbc.JdbcConfiguration;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Import({JdbcConfiguration.class})
@ComponentScan
@Order(1)
public class JdbcConfig {
}
