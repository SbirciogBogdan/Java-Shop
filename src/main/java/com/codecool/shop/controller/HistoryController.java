package com.codecool.shop.controller;

import com.codecool.shop.config.Template;
import com.codecool.shop.dao.DataStore;
import com.codecool.shop.model.Cart;
import com.codecool.shop.model.Order;
import com.codecool.shop.model.User;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"history.html"})
public class HistoryController extends HttpServlet {
    TemplateEngine engine;
    WebContext context;
    DataStore dataStore;
    HttpSession session;
    Cart cart;
    List<Order> orderList;

    private void setData(HttpServletRequest req, HttpServletResponse resp) {
        dataStore = DataStore.getInstance();
        session = req.getSession();
        engine = Template.getTemplateEngine(req.getServletContext());
        context = new WebContext(req, resp, req.getServletContext());

        //get orders of logged in user and add to context
        orderList = dataStore.orderDao.getOrdersOf((User) session.getAttribute("user"));
        context.setVariable("orders", orderList);

        //create shopping cart if not present
        cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }

        //get and add categories and suppliers to context
        context.setVariable("categories", dataStore.categoryDao.getAll());
        context.setVariable("suppliers", dataStore.supplierDao.getAll());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        setData(req, resp);

        //send context to template
        engine.process("history.html", context, resp.getWriter());
    }
}
