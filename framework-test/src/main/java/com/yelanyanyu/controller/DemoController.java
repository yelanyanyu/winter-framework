package com.yelanyanyu.controller;

import com.yelanyanyu.bean.Member;
import com.yelanyanyu.webmvc.annotation.GetMapping;
import com.yelanyanyu.webmvc.annotation.RestController;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@RestController
public class DemoController {
    @GetMapping("/t3")
    public Member t3() {
        return new Member("asdfasf", 100);
    }

    @GetMapping("/t4")
    public String t4() {
        return "{\"name\":\"asdfasf\",\"id\":100}";
    }

    @GetMapping("/t5")
    public byte[] t5() {
        return new byte[100];
    }
}
