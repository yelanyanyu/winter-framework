package com.yelanyanyu.web;

import com.yelanyanyu.web.bean.Param;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 *
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dispatcher {
    boolean isRest;
    boolean isResponseBody;
    boolean isVoid;
    Pattern urlPattern;
    Object controller;
    Method handlerMethod;
    Param[] methodParameters;

}
