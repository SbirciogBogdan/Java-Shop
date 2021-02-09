package com.codecool.shop.controller;

import com.codecool.shop.config.Template;
import com.codecool.shop.dao.DataStore;
import com.codecool.shop.model.Cart;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = {"/confirmation.html"})
public class ConfirmationController extends HttpServlet {
    DataStore dataStore;
    TemplateEngine engine;
    WebContext context;
    HttpSession session;
    Cart cart;

    private void setData(HttpServletRequest req, HttpServletResponse resp) {
        dataStore = DataStore.getInstance();
        session = req.getSession();
        engine = Template.getTemplateEngine(req.getServletContext());
        context = new WebContext(req, resp, req.getServletContext());

        //create shopping cart if not present
        cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        setData(req, resp);

        //send context to template
        engine.process("confirmation.html", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        setData(req, resp);
        
        //send context to template
        engine.process("confirmation.html", context, resp.getWriter());
    }
}
