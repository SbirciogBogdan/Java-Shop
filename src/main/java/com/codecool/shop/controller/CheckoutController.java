package com.codecool.shop.controller;

import com.codecool.shop.config.Template;
import com.codecool.shop.dao.DataStore;
import com.codecool.shop.model.*;
import com.codecool.shop.utilities.JSONFiler;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;

@WebServlet(urlPatterns = {"/checkout.html"})
public class CheckoutController extends HttpServlet {
    DataStore dataStore;
    TemplateEngine engine;
    WebContext context;
    HttpSession session;
    Cart cart;
    JSONFiler filer;

    private void setData(HttpServletRequest req, HttpServletResponse resp) {
        dataStore = DataStore.getInstance();
        session = req.getSession();
        engine = Template.getTemplateEngine(req.getServletContext());
        context = new WebContext(req, resp, req.getServletContext());
        filer = new JSONFiler(getServletContext().getRealPath("/"));

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
        engine.process("checkout.html", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        setData(req, resp);

        if (cart.getNumberOfItems() > 0) {
            //get form data
            String name = req.getParameter("name");
            String email = req.getParameter("email");
            String phone = req.getParameter("phone");
            String BillCountry = req.getParameter("BillCountry");
            String BillCity = req.getParameter("BillCity");
            String BillZipcode = req.getParameter("BillZipcode");
            String BillStreetAddress = req.getParameter("BillStreetAddress");
            String ShipCountry = req.getParameter("ShipCountry");
            String ShipCity = req.getParameter("ShipCity");
            String ShipZipcode = req.getParameter("ShipZipcode");
            String ShipStreetAddress = req.getParameter("ShipStreetAddress");

            //process user data
            User user = (User) session.getAttribute("user");
            Address bill = new Address(BillCountry, BillCity, BillZipcode, BillStreetAddress);
            Address ship = new Address(ShipCountry, ShipCity, ShipZipcode, ShipStreetAddress);

            //user is not logged in
            if (user == null) {
                User unsignedUser = new User(name, email, null, phone, bill, ship, UserStatus.UNSIGNED);
                dataStore.userDao.add(unsignedUser);
                session.setAttribute("unsignedUser", unsignedUser);
                user = unsignedUser;
            }
            //user is logged in
            else {
                //user is missing billing info
                if (user.getBilling() == null) {
                    //use form data to update billing info
                    user.setBilling(bill);
                    user.setShipping(ship);
                }
            }

            //create new order
            Order order = new Order(cart, user);

            //add order to store and session
            dataStore.orderDao.add(order);
            session.setAttribute("order", order);

            //log event and update log file
            order.logEvent(new Date(), "Order created. Status = " + order.getStatus());
            filer.writeOrderLog(order, "/logs");

            //empty the cart and update session
            session.setAttribute("cart", new Cart());

            //redirect to payment page
            RequestDispatcher requestDispatcher = req.getRequestDispatcher("payment.html");
            requestDispatcher.forward(req, resp);
        }

        //send context to template
        engine.process("checkout.html", context, resp.getWriter());
    }
}
