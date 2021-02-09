package com.codecool.shop.controller;

import com.codecool.shop.config.Template;
import com.codecool.shop.dao.DataStore;
import com.codecool.shop.model.Cart;
import com.codecool.shop.model.Category;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.Supplier;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = {"/", "index.html"})
public class ProductController extends HttpServlet {
    TemplateEngine engine;
    WebContext context;
    DataStore dataStore;
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

        //get and add categories and suppliers to context
        context.setVariable("categories", dataStore.categoryDao.getAll());
        context.setVariable("suppliers", dataStore.supplierDao.getAll());
    }

    private void applyFilters(String categoryId, String supplierId) {
        //filter the products and save filters in session (for use in doPost)
        if (categoryId != null) {
            //apply category filter and store it in session
            Category category = dataStore.categoryDao.find(Integer.parseInt(categoryId));
            context.setVariable("products", dataStore.productDao.getBy(category));
            session.setAttribute("category", categoryId);
            session.removeAttribute("supplier");
        } else if (supplierId != null) {
            //apply supplier filter and store it in session
            Supplier supplier = dataStore.supplierDao.find(Integer.parseInt(supplierId));
            context.setVariable("products", dataStore.productDao.getBy(supplier));
            session.setAttribute("supplier", supplierId);
            session.removeAttribute("category");
        } else {
            //no filter = get all products
            context.setVariable("products", dataStore.productDao.getAll());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        setData(req, resp);

        //get and apply filters from get parameters (URL)
        String categoryId = req.getParameter("category");
        String supplierId = req.getParameter("supplier");
        String checkAll = req.getParameter("all");
        if (checkAll != null) {
            context.setVariable("products", dataStore.productDao.getAll());
            session.removeAttribute("category");
            session.removeAttribute("supplier");
        } else
            applyFilters(categoryId, supplierId);

        //send context to template
        engine.process("index.html", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        setData(req, resp);

        //get and apply filters from session
        String categoryId = (String) session.getAttribute("category");
        String supplierId = (String) session.getAttribute("supplier");
        applyFilters(categoryId, supplierId);

        //if product added to cart
        String productId = req.getParameter("productId");
        if (productId != null) {
            //get product by posted id
            int id = Integer.parseInt(productId);
            Product product = dataStore.productDao.find(id);

            //add product to cart
            cart.addProduct(product, product.getCurrentPrice());

            //update cart in session
            session.setAttribute("cart", cart);
        }

        //send context to template
        engine.process("index.html", context, resp.getWriter());
    }
}
