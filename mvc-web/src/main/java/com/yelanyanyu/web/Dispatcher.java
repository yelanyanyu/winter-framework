package com.yelanyanyu.web;

import com.yelanyanyu.web.annotation.ResponseBody;
import com.yelanyanyu.web.bean.Param;
import com.yelanyanyu.web.bean.Result;
import com.yelanyanyu.web.util.JsonUtils;
import com.yelanyanyu.web.util.PathUtils;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.regex.Matcher;
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
    private static final Result NOT_PROCESSED = new Result(false, null);
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

    /**
     * truly invoke the controller's method after all parameters are filled.
     *
     * @param url      /a/1/b/2 and the like.
     * @param request  .
     * @param response .
     * @return method return result
     */
    Result process(String url, HttpServletRequest request, HttpServletResponse response) throws ServletException {
        log.debug("process, url: {}", url);
        Matcher matcher = urlPattern.matcher(url);
        // if the match be successful.
        if (matcher.matches()) {
            // fill arguments
            Object[] arguments = new Object[methodParameters.length];
            for (int i = 0; i < arguments.length; i++) {
                Param currentParam = methodParameters[i];
                arguments[i] = switch (currentParam.getParamType()) {
                    case REQUEST_PARAM -> {
                        log.debug("request param...");
                        String parameter = request.getParameter(currentParam.getName());
                        try {
                            yield convertToType(currentParam.getClassType(), parameter);
                        } catch (IllegalArgumentException e) {
                            log.error("error: {}", e.getClass().getSimpleName());
                            throw new RuntimeException(e);
                        }
                    }
                    case REQUEST_BODY -> {
                        // request 为 json 数据
                        BufferedReader reader = null;
                        try {
                            reader = request.getReader();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        yield JsonUtils.readerToBean(reader, currentParam.getClassType());
                    }
                    case PATH_VARIABLE -> {
                        String v = matcher.group(currentParam.getName());
                        if (v.isEmpty()) {
                            v = currentParam.getDefaultValue();
                        }
                        yield convertToType(currentParam.getClassType(), v);
                    }
                    case SERVLET_VARIABLE -> {
                        Class<?> classType = currentParam.getClassType();
                        if (classType == HttpServletRequest.class) {
                            yield request;
                        } else if (classType == HttpServletResponse.class) {
                            yield response;
                        } else if (classType == HttpSession.class) {
                            yield request.getSession();
                        } else if (classType == ServletContext.class) {
                            yield request.getServletContext();
                        } else {
                            throw new ServletException("Incorrect parameter type " + classType.getName()
                                    + " for " + this.controller);
                        }
                    }
                };
            }
            // invoke controller's method
            Object result = null;
            log.debug("arguments: {}", Arrays.toString(arguments));
            try {
                result = this.handlerMethod.invoke(this.controller, arguments);
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.debug("arguments: {}", Arrays.toString(arguments));
                throw new RuntimeException(e);
            }
            log.debug("method {}.{} are invoked", controller.getClass().getSimpleName(), handlerMethod.getName());
            return new Result(true, result);
        }
        return NOT_PROCESSED;
    }

    /**
     * convert parameter to the base data type.
     *
     * @param targetType .
     * @param parameter  .
     * @return .
     * @throws ServletException .
     */
    Object convertToType(Class<?> targetType, String parameter) throws ServletException {
        if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(parameter);
        } else if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(parameter);
        } else if (targetType == Long.class || targetType == long.class) {
            return Long.parseLong(parameter);
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(parameter);
        } else if (targetType == float.class || targetType == Float.class) {
            return Float.parseFloat(parameter);
        } else if (targetType == byte.class || targetType == Byte.class) {
            return Byte.parseByte(parameter);
        } else if (targetType == short.class || targetType == Short.class) {
            return Short.parseShort(parameter);
        } else if (targetType == String.class) {
            return parameter;
        } else {
            throw new ServletException(String.format(
                    "Cannot convert parameter '%s' to type '%s'.",
                    parameter, targetType.getSimpleName()
            ));
        }
    }
}
