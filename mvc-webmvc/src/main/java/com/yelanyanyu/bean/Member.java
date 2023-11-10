package com.yelanyanyu.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Data
@AllArgsConstructor
public class Member {
    String name;
    String gender;
    Integer age;
}
