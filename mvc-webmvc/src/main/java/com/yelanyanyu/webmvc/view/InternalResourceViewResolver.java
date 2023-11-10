package com.yelanyanyu.webmvc.view;

import jakarta.annotation.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class InternalResourceViewResolver extends UrlBasedViewResolver {
    @Override
    public void init() {

    }

    @Override
    public View resolveViewName(String viewName, @Nullable Map<String, Object> model, HttpServletRequest req, HttpServletResponse resp) {
        return super.resolveViewName(viewName, model, req, resp);
    }
}
