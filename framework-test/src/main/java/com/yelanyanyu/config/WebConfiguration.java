package com.yelanyanyu.config;

import com.yelanyanyu.annotation.Bean;
import com.yelanyanyu.annotation.Configuration;
import com.yelanyanyu.web.view.InternalResourceViewResolver;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Configuration
public class WebConfiguration {
    @Bean
    public InternalResourceViewResolver internalResourceViewResolver() {
        InternalResourceViewResolver irv = new InternalResourceViewResolver();
        irv.setPrefix("/WEB-INF/views/");
        irv.setSuffix(".jsp");
        return irv;
    }
}
