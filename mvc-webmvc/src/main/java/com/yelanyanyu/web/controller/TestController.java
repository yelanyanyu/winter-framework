package com.yelanyanyu.web.controller;

import com.yelanyanyu.annotation.*;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@RestController
@Slf4j
public class TestController {
    @GetMapping("/t1/t2/{id}/{card}")
    @ResponseBody
    public String t1(@PathVariable("id") Integer id,
                     @PathVariable("card") String card) {

        return "";
    }

    @PostMapping("/t2")
    public String t2(HttpServletRequest request, HttpServletResponse response, HttpSession session,
                     @RequestParam(value = "abc", defaultValue = "NONE") String abc) {
        return "";
    }
}
