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
public class JstlView extends AbstractUrlBasedView {
    public JstlView(String url) {
        super(url);
    }

    @Override
    public void renderMergedOutputModel(Map<String, Object> mode, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.debug("JstlView -> renderMergedOutputModel");
        if (mode != null) {
            mode.forEach(request::setAttribute);
        }
        request.getRequestDispatcher(url).forward(request, response);
    }
}
