package com.yelanyanyu.service;

import com.yelanyanyu.annotation.Value;
import com.yelanyanyu.webmvc.annotation.Service;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Service
@Slf4j
@Getter
public class TestService {
    @Getter
    @Value("${winter.aaa.bbb:123456}")
    private Integer test1;

    @Value("${winter.datasource.username:NONE}")
    private String username;
    @Value("${winter.datasource.url:NONE}")
    private String url;

    @Value("${winter.datasource.password}")
    private String password;
    @Value("${winter.datasource.driver-class-name}")
    private String driverClassName;


}
