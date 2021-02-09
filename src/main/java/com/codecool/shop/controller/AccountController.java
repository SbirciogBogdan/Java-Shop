package com.codecool.shop.controller;

import com.codecool.shop.config.Template;
import com.codecool.shop.dao.DataStore;
import com.codecool.shop.model.Address;
import com.codecool.shop.model.User;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = {"/account.html"})
public class AccountController extends HttpServlet {
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

        engine.process("account.html", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        setData(req, resp);

        //get new data
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String phone = req.getParameter("phone");
        String BillCountry = req.getParameter("BillCountry");
        String BillCity = req.getParameter("BillCity");
        String BillZipcode = req.getParameter("BillZipcode");
        String BillStreetAddress = req.getParameter("BillStreetAddress");
        String ShipCountry = req.getParameter("ShipCountry");
        String ShipCity = req.getParameter("ShipCity");
        String ShipZipcode = req.getParameter("ShipZipcode");
        String ShipStreetAddress = req.getParameter("ShipStreetAddress");

        //update user
        Address billing = new Address(BillCountry, BillCity, BillZipcode, BillStreetAddress);
        Address shipping = new Address(ShipCountry, ShipCity, ShipZipcode, ShipStreetAddress);
        User user = (User) session.getAttribute("user");
        dataStore.userDao.updateUser(user, name, email, password, phone, billing, shipping);
        session.setAttribute("user", user);

        //send context to template
        engine.process("account.html", context, resp.getWriter());
    }
}
