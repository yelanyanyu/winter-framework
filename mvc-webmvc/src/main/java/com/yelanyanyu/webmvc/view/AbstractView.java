package com.yelanyanyu.webmvc.view;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Slf4j
public abstract class AbstractView implements View {
    @Override
    public void render(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.debug("Abstract view render with attributes: {}", model);
        renderMergedOutputModel(model, request, response);
    }

    /**
     * <p>The first step will be preparing the request: In the JSP case,
     * 	this would mean setting model objects as request attributes.
     * 	The second step will be the actual rendering of the view,
     * 	for example including the JSP via a RequestDispatcher.
     * @param mode
     * @param request
     * @param response
     * @throws Exception
     */
    public abstract void renderMergedOutputModel(Map<String, Object> mode, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
