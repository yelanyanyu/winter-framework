package com.yelanyanyu.controller;

import com.yelanyanyu.annotation.Autowired;
import com.yelanyanyu.bean.Member;
import com.yelanyanyu.service.TestService;
import com.yelanyanyu.webmvc.ModelAndView;
import com.yelanyanyu.webmvc.annotation.*;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Slf4j
@Controller
public class TestController {
    @Autowired
    private TestService testService;
    @GetMapping("/t1/t2/{id}/{card}")
    public ModelAndView t1(@PathVariable("id") Integer id,
                           @PathVariable("card") String card) {
        log.info("id: {}, card: {}", id, card);
        ModelAndView modelAndView = new ModelAndView("forward:success");
        modelAndView.addModel("msg", "nihao!!!!!!!");
        modelAndView.addModel("error", "error...");
        modelAndView.addModel("bean", new Member("zhangsan", "nv", 500));
        modelAndView.addModel("testService", testService.getTest1());
        modelAndView.addModel("username", testService.getUsername());
        modelAndView.addModel("url", testService.getUrl());
        modelAndView.addModel("pwd", testService.getPassword());
        modelAndView.addModel("driver", testService.getDriverClassName());
        return modelAndView;
    }

    @PostMapping("/t2")
    public String t2(HttpServletRequest request, HttpServletResponse response, HttpSession session,
                     @RequestParam(value = "abc", defaultValue = "NONE") String abc) {
        return "";
    }

    @GetMapping("/t3")
    @ResponseBody
    public Member t3() {
        Member member = new Member("lisi", "nan", 200);
        return member;
    }
}
