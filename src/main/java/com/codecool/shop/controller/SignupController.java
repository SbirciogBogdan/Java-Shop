package com.codecool.shop.controller;

import com.codecool.shop.config.Template;
import com.codecool.shop.dao.DataStore;
import com.codecool.shop.model.User;
import com.codecool.shop.model.UserStatus;
import com.codecool.shop.utilities.Mailer;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = {"/signup.html"})
public class SignupController extends HttpServlet {
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

        engine.process("signup.html", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        setData(req, resp);

        //get post parameters
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String phone = req.getParameter("phone");

        //check if user is already registered
        if (dataStore.userDao.isSignedUp(email)) {
            session.removeAttribute("user");
            session.setAttribute("signupError", "Email already used in another account. Use another email for this account.");
        } else { //add new user
            User user = new User(name, email, password, phone, null, null, UserStatus.SIGNED);
            dataStore.userDao.add(user);

            //update session
            session.setAttribute("user", user);
            session.removeAttribute("signupError");

            //send welcome mail
            String subject = "[CodeCoolShop] Welcome to our shop!";
            String message = "Your account was successfully created:" +
                    "\nmail: " + email +
                    "\npassword: " + password +
                    "\n\nFor a better shopping experience update your billing info.";
            (new Mailer("pythonsendmailtest75@gmail.com", "lpiiamlxlfsnzwxs", email, subject, message)).start();
        }

        //send context to template
        engine.process("signup.html", context, resp.getWriter());
    }
}
