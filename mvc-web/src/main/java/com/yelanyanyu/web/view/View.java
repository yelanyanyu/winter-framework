package com.yelanyanyu.web.view;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public interface View {
    /**
     * determine response-Header's content-type. For this version, this method remains unimplemented, hence, please refrain from utilizing it.
     *
     * @return .
     */
    default String getContentType() {
        return null;
    }

    /**
     * For this version, it truly sends http response back, and set models to request attribute.
     *
     * @param model    .
     * @param request  .
     * @param response .
     * @throws Exception .
     */
    void render(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception;

}
