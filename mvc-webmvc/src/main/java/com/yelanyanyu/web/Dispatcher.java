package com.yelanyanyu.web;

import com.yelanyanyu.annotation.ResponseBody;
import com.yelanyanyu.web.bean.Param;
import com.yelanyanyu.web.util.PathUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.regex.Pattern;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Dispatcher {
    boolean isRest;
    boolean isResponseBody;
    boolean isVoid;
    Pattern urlPattern;
    Object controller;
    Method handlerMethod;
    Param[] methodParameters;

    public Dispatcher(String httpStatus, boolean isRest, Object controller, Method handlerMethod, String urlPattern) {
        this.isRest = isRest;
        this.controller = controller;
        this.handlerMethod = handlerMethod;
        this.isVoid = handlerMethod.getReturnType() == Void.class;
        this.isResponseBody = handlerMethod.getAnnotation(ResponseBody.class) != null;
        this.urlPattern = PathUtils.compile(urlPattern);
        Parameter[] parameters = handlerMethod.getParameters();
        this.methodParameters = new Param[parameters.length];
        Annotation[][] parameterAnnotations = handlerMethod.getParameterAnnotations();

        // Instantiating the parameters provided to the method.
        for (int i = 0; i < this.methodParameters.length; i++) {
            methodParameters[i] = new Param(httpStatus, parameters[i], parameterAnnotations[i], handlerMethod);
        }

        log.debug("mapping {} to handler {}.{}", urlPattern, controller.getClass().getSimpleName(), handlerMethod.getName());
        if (log.isDebugEnabled()) {
            for (Param param : this.methodParameters) {
                log.debug("> param: {}", param.toString());
            }
        }

    }
}
