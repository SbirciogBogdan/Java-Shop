package com.codecool.shop.controller;

import com.codecool.shop.config.Template;
import com.codecool.shop.dao.DataStore;
import com.codecool.shop.model.Cart;
import com.codecool.shop.model.Order;
import com.codecool.shop.model.User;
import com.codecool.shop.utilities.JSONFiler;
import com.codecool.shop.utilities.Mailer;
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

@WebServlet(urlPatterns = {"/payment.html"})
public class PaymentController extends HttpServlet {
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

    private boolean creditPayment(String number, String holder, String expmm, String expyy, String cvv) {
        //TO DO: credit card payment processing
        return false; //set to always fail for testing
    }

    private boolean paypalPayment(String name, String pass) {
        //TO DO: paypal payment processing
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        setData(req, resp);

        //send context to template
        engine.process("payment.html", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        setData(req, resp);

        //get payment method
        String paymentMethod = req.getParameter("paymentMethod");

        //execute payment and get result
        if(paymentMethod != null) {
            boolean paymentOK = false;
            if (paymentMethod.equals("credit")) {
                String number = req.getParameter("cardNumber");
                String holder = req.getParameter("cardHolder");
                String expmm = req.getParameter("expiryMonth");
                String expyy = req.getParameter("expiryYear");
                String cvv = req.getParameter("cvv");
                paymentOK = creditPayment(number, holder, expmm, expyy, cvv);
            } else if (paymentMethod.equals("paypal")) {
                String username = req.getParameter("username");
                String password = req.getParameter("password");
                paymentOK = paypalPayment(username, password);
            }

            if(paymentOK) {
                //change order status
                Order order = (Order) session.getAttribute("order");
                order.pay();
                dataStore.orderDao.setPayed(order);

                //update session
                session.removeAttribute("order");
                session.setAttribute("orderName", order.getName());
                session.removeAttribute("paymentError");

                //send confirmation mail
                User user = (User) session.getAttribute("user");
                if(user == null)
                    user = (User) session.getAttribute("unsignedUser");
                String subject = "[CodeCoolShop] Order Confirmed!";
                String message = order.getName() + " was successfully payed and is ready for shipment." +
                        "\nOrder contains " + order.getCart().getNumberOfItems() + " items " +
                        "\nwith a total price of " + order.getCart().getTotalPrice() + ". " +
                        "\n\nThank you!";
                (new Mailer("pythonsendmailtest75@gmail.com", "lpiiamlxlfsnzwxs", user.getEmail(), subject, message)).start();

                //save json file with order data
                filer.writeOrder(order, "/orders");

                //log event and update log file
                order.logEvent(new Date(), "Order payed. Status = " + order.getStatus());
                filer.writeOrderLog(order, "/logs");

                //redirect to confirmation page
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("confirmation.html");
                requestDispatcher.forward(req, resp);
            } else {
                session.setAttribute("paymentError", true);
            }
        } else { //no payment method selected yet
            session.removeAttribute("paymentError");
        }

        //send context to template
        engine.process("payment.html", context, resp.getWriter());
    }
}
