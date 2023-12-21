package com.yelanyanyu.controller;

import com.yelanyanyu.bean.Member;
import com.yelanyanyu.web.annotation.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@RestController
@Slf4j
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

    @GetMapping("/t6")
    public Member t6(@RequestBody Member member, @RequestParam("id") Integer id) {
        log.info("id: {}", id);
        return member;
    }

    @GetMapping("/t7/{name}/{id}")
    public Member t7(@PathVariable("name") String name, @PathVariable("id") Integer id) {
        return new Member(name, id);
    }
}
