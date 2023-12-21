package com.yelanyanyu.web.view;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

/**
 * The ViewResolver, added by the user, can be annotated as @Bean within a bean that annotated with @Configuration to incorporate it into the application context.
 *
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public interface ViewResolver {
    /**
     * init ViewResolver
     */
    void init();

    /**
     * Cast viewName string to View bean, then invoke the view, send data back.
     * More detail is that if view is null, then create one or
     *
     * @param viewName .
     * @param model    .
     * @param req      .
     * @param resp     .
     * @return .
     */
    View resolveViewName(String viewName, @Nullable Map<String, Object> model, HttpServletRequest req, HttpServletResponse resp);
}
