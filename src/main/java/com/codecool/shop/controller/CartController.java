package com.codecool.shop.controller;

import com.codecool.shop.config.Template;
import com.codecool.shop.dao.DataStore;
import com.codecool.shop.model.Cart;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.User;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;

@WebServlet(urlPatterns = {"/cart.html"})
@MultipartConfig
public class CartController extends HttpServlet {
    TemplateEngine engine;
    WebContext context;
    HttpSession session;
    Cart cart;
    DataStore dataStore;

    private void setData(HttpServletRequest req, HttpServletResponse resp) {
        engine = Template.getTemplateEngine(req.getServletContext());
        context = new WebContext(req, resp, req.getServletContext());
        session = req.getSession();
        dataStore = DataStore.getInstance();

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

        engine.process("cart.html", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        setData(req, resp);

        //get parameters
        String quantityParameter = req.getParameter("quantity");
        String productIdParameter = req.getParameter("productId");
        String operationParameter = req.getParameter("operation");

        //if quantity modified in form - update cart
        if (quantityParameter != null && operationParameter.equals("updateQuantity")) {
            //get product
            int id = Integer.parseInt(productIdParameter);
            Product product = dataStore.productDao.find(id);

            //get current quantities
            int newQuantity = Integer.parseInt(quantityParameter);
            int oldQuantity = cart.getQuantityOfProduct(product);

            //update cart to match quantities
            if (newQuantity > oldQuantity) { //add products
                for (int i = 1; i <= newQuantity - oldQuantity; i++)
                    cart.addProduct(product, product.getCurrentPrice());
            }
            if (newQuantity < oldQuantity) { //remove products
                for (int i = 1; i <= oldQuantity - newQuantity; i++)
                    cart.removeProduct(product);
            }
        }

        //if "save my cart" activated
        User user = (User) session.getAttribute("user");
        if (user != null && operationParameter.equals("saveCart")) {
            //update datastore user
            dataStore.userDao.updateUserCart(user, cart);

            //update session data
            user.setMyCart(cart);
            session.setAttribute("user", user);
            session.setAttribute("saveTime", new Date());
        }

        //update cart in session
        session.setAttribute("cart", cart);

        engine.process("cart.html", context, resp.getWriter());
    }
}
