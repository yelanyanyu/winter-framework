package com.yelanyanyu.app.controller;

import com.yelanyanyu.annotation.Autowired;
import com.yelanyanyu.app.bean.Monster;
import com.yelanyanyu.jdbc.JdbcTemplate;
import com.yelanyanyu.web.annotation.GetMapping;
import com.yelanyanyu.web.annotation.PathVariable;
import com.yelanyanyu.web.annotation.RestController;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@RestController
public class MonsterController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/get/{id}")
    public Monster findMonsterById(@PathVariable("id") Integer id) {
        return jdbcTemplate.queryForObject("select * from monster where id = ?", Monster.class, id);
    }
}
