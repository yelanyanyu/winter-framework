package com.yelanyanyu.app.bean;

import lombok.Data;

import java.util.Date;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Data
public class Monster {
    private int id;
    private int age;
    private Date birthday;
    private String email;
    private String gender;
    private String name;
    private double salary;
}
