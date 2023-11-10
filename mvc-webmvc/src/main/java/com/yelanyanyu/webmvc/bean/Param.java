package com.yelanyanyu.webmvc.bean;

import com.yelanyanyu.webmvc.annotation.PathVariable;
import com.yelanyanyu.webmvc.annotation.RequestBody;
import com.yelanyanyu.webmvc.annotation.RequestParam;
import com.yelanyanyu.webmvc.exception.ServletException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Data
@Slf4j
public class Param {
    String name;
    ParamType paramType;
    Class<?> classType;
    String defaultValue;

    public Param(String httpStatus, Parameter parameter, Annotation[] annotations, Method method) {
        PathVariable pathVariable = getAnnotation(annotations, PathVariable.class);
        RequestBody requestBody = getAnnotation(annotations, RequestBody.class);
        RequestParam requestParam = getAnnotation(annotations, RequestParam.class);
        // check if a parameter has more than one annotation.
        int cnt = checkAnnotation(method, pathVariable, requestBody, requestParam);
        this.name = parameter.getName();
        this.classType = parameter.getType();
        if (pathVariable != null) {
            this.paramType = ParamType.PATH_VARIABLE;
            this.name = pathVariable.value();
        }
        if (requestBody != null) {
            this.paramType = ParamType.REQUEST_BODY;
        }
        if (requestParam != null) {
            this.name = requestParam.value();
            this.paramType = ParamType.REQUEST_PARAM;
            this.defaultValue = requestParam.defaultValue();
        }
        if (cnt == 0) {
            this.paramType = ParamType.SERVLET_VARIABLE;
            if ((this.classType != HttpServletRequest.class)
                    && (this.classType != HttpServletResponse.class)
                    && (this.classType != HttpSession.class)
                    && (this.classType != ServletContext.class)) {
                log.error("classType: {}.{}", method.getName(), this.classType);
                throw new ServletException("Unsupported parameter type " + this.classType);
            }
        }
    }

    <A extends Annotation> A getAnnotation(Annotation[] annotations, Class<A> annoClass) {
        for (Annotation anno : annotations) {
            if (annoClass.isInstance(anno)) {
                return (A) anno;
            }
        }
        return null;
    }

    int checkAnnotation(Method method, Annotation... vars) {
        int cnt = 0;
        for (Annotation var : vars) {
            if (var != null) {
                cnt++;
            }
        }
        if (cnt > 1) {
            throw new ServletException(String.format(
                    "found %d annotation(s) in parameter %s.%s", cnt,
                    method.getName(), this.name
            ));
        }
        return cnt;
    }
}
