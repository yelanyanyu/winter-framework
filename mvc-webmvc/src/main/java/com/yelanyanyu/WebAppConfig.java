package com.yelanyanyu;

import com.yelanyanyu.annotation.ComponentScan;
import com.yelanyanyu.annotation.Configuration;
import com.yelanyanyu.annotation.Import;
import com.yelanyanyu.webmvc.WebMvcConfiguration;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@ComponentScan(value = {"com.yelanyanyu.controller", "com.yelanyanyu.config"})
@Configuration
@Import(WebMvcConfiguration.class)
public class WebAppConfig {
}
