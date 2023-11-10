package com.yelanyanyu.webmvc.view;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public abstract class AbstractUrlBasedView extends AbstractView {
    /**
     * view resource location, such like '/WEB-INF/view/index.jsp'
     */
    String url;

    public AbstractUrlBasedView() {
    }

    public AbstractUrlBasedView(String url) {
        setUrl(url);
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
