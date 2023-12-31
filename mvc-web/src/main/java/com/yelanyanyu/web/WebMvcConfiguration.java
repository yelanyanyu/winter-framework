package com.yelanyanyu.web;

import com.yelanyanyu.annotation.Bean;
import com.yelanyanyu.annotation.Configuration;
import jakarta.servlet.ServletContext;


/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Configuration
public class WebMvcConfiguration {
    static ServletContext context;

    static void setServletContext(ServletContext ctx) {
        context = ctx;
    }

    @Bean
    public ServletContext servletContext() {
        return context;
    }
}
