package com.codecool.shop.controller;

import com.codecool.shop.config.Template;
import com.codecool.shop.dao.DataStore;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = {"/logout.html"})
public class LogoutController extends HttpServlet {
    DataStore dataStore;
    TemplateEngine engine;
    WebContext context;
    HttpSession session;

    private void setData(HttpServletRequest req, HttpServletResponse resp) {
        dataStore = DataStore.getInstance();
        session = req.getSession();
        engine = Template.getTemplateEngine(req.getServletContext());
        context = new WebContext(req, resp, req.getServletContext());

        //get and add categories and suppliers to context (for sidebar)
        context.setVariable("categories", dataStore.categoryDao.getAll());
        context.setVariable("suppliers", dataStore.supplierDao.getAll());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        setData(req, resp);

        //get and add categories and suppliers to context (for sidebar)
        context.setVariable("categories", dataStore.categoryDao.getAll());
        context.setVariable("suppliers", dataStore.supplierDao.getAll());

        //clean session
        session.removeAttribute("user");
        session.removeAttribute("loginError");
        session.removeAttribute("signupError");
        session.removeAttribute("saveTime");
        session.removeAttribute("cart");
        session.removeAttribute("order");
        session.removeAttribute("orderName");

        //send context to template
        engine.process("logout.html", context, resp.getWriter());
    }
}
