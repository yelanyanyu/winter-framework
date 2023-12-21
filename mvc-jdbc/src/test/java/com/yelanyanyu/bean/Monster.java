package com.yelanyanyu.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Monster {
    private int id;
    private int age;
    private Date birthday;
    private String email;
    private String gender;
    private String name;
    private double salary;

    public Monster(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
