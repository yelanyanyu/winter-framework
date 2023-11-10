package com.yelanyanyu.webmvc.bean;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public enum ParamType {
    /**
     * '@PathVariable("id")', 'id' is PATH_VARIABLE
     */
    PATH_VARIABLE,
    /**
     * ~/aaa?p1=v1&p2=v2... p1,p2 is REQUEST_PARAM
     */
    REQUEST_PARAM,
    /**
     * '@ResponseBody'
     */
    REQUEST_BODY,
    /**
     * req, resp and the like.
     */
    SERVLET_VARIABLE
}
