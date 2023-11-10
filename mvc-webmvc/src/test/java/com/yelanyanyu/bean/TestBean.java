package com.yelanyanyu.bean;

import lombok.Data;

import java.util.List;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Data
public class TestBean {
    String name;
    String phone;
    String address;
    Integer age;
//    List<Friend> friends;
    Friend[] friends;
}
