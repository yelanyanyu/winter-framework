package com.bean;

import com.yelanyanyu.annotation.*;
import jakarta.annotation.PostConstruct;
import lombok.Data;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Import(Class02.class)
@Configuration
@Data
public class Class01 {
    @Autowired
    private Class07 class07;
    @Value("${a.b.c}")
    private String abcd;
    @Bean
    public class03 class03(@Autowired Class04 class04, @Value("${a.b.c:1000}")String name) {
        System.out.println("===================test-bean: " + class04);
        System.out.println("===================test-value: " + name);
        return new class03();
    }

    @PostConstruct
    public void init() {
        System.out.println("++++++++++++++++++");
    }
}
