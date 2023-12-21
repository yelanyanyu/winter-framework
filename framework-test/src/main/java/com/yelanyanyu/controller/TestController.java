package com.yelanyanyu.controller;

import com.yelanyanyu.bean.Member;
import com.yelanyanyu.web.ModelAndView;
import com.yelanyanyu.web.annotation.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Controller
@Slf4j
public class TestController {
    @GetMapping("/t1")
    public ModelAndView t1() {
        ModelAndView success = new ModelAndView("forward:success");
        return success;
    }

    @PostMapping("/t2/{id}")
    @ResponseBody
    public Member t2(@PathVariable("id") Integer id, @RequestParam(value = "name", defaultValue = "NODE") String name) {
        log.info("id: {}", id);
        log.info("name: {}", name);
        return new Member(name, id);
    }
}
