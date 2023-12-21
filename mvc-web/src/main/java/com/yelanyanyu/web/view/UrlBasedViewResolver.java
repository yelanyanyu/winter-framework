package com.yelanyanyu.web.view;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public abstract class UrlBasedViewResolver implements ViewResolver {
    private static final String FORWARD_URL_PREFIX = "forward:";
    private static final String REDIRECT_URL_PREFIX = "redirect:";

    private String prefix = "";
    private String suffix = "";

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public View resolveViewName(String viewName, @Nullable Map<String, Object> model, HttpServletRequest req, HttpServletResponse resp) {
        if (!viewName.startsWith(FORWARD_URL_PREFIX)) {
            return null;
        }
        viewName = viewName.substring(FORWARD_URL_PREFIX.length());
        return new JstlView(prefix + viewName + suffix);
    }
}
