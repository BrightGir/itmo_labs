package ru.bright.weblab3;

import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class InitServlet extends HttpServlet {

    @Override
    public void init() {
        System.out.println("Initializing application at " + new java.util.Date());
        FacesContext.getCurrentInstance().getApplication()
                .evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{resultHistoryBean}", ResultHistoryBean.class);
        System.out.println("resultHistoryBean initialized");
    }
}