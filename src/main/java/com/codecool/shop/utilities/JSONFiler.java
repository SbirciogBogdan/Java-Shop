package com.codecool.shop.utilities;

import com.codecool.shop.model.Item;
import com.codecool.shop.model.Order;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JSONFiler {
    private final String basePath;

    public JSONFiler(String basePath) {
        this.basePath = basePath;
    }

    public void writeOrder(Order order, String path) {
        JSONObject jsonOrder = createJSONOrder(order);
        try {
            //write file
            String filePath = basePath + path + "/Order-" + order.getId() + ".json";
            FileWriter file = new FileWriter(filePath);
            file.write(jsonOrder.toString(3));
            file.close();
        } catch (IOException e) {
            System.out.println("JSON Order write error: " + e.getMessage());
        }
    }

    private JSONObject createJSONOrder(Order order) {
        JSONObject jsonObject = new JSONObject();

        //add general info
        jsonObject.put("id", order.getId());
        jsonObject.put("total", order.getCart().getTotalPrice());
        jsonObject.put("user", order.getUser().getName());
        jsonObject.put("date", order.getDate());

        //add shipping address
        HashMap<String, String> shipping = new HashMap<>();
        shipping.put("country", order.getUser().getShipping().getCountry());
        shipping.put("city", order.getUser().getShipping().getCity());
        shipping.put("zipcode", order.getUser().getShipping().getZipcode());
        shipping.put("address", order.getUser().getShipping().getHomeAddress());
        jsonObject.put("shipping", shipping);

        //add products
        List<HashMap<String, String>> products = new ArrayList<>();
        for (Item item : order.getCart().getItems()) {
            HashMap<String, String> product = new HashMap<>();
            product.put("id", String.valueOf(item.getProduct().getId()));
            product.put("name", item.getProduct().getName());
            product.put("supplier", item.getProduct().getSupplier().getName());
            product.put("quantity", String.valueOf(item.getQuantity()));
            products.add(product);
        }
        jsonObject.put("products", products);

        return jsonObject;
    }

    public void writeOrderLog(Order order, String path) {
        JSONObject jsonOrder = createJSONOrderLog(order);
        try {
            //write file
            String filePath = basePath + path + "/Order-" + order.getId() + " "
                    + order.getDate().toString().replace(' ', '-').replace(':', '-')
                    + ".json";
            FileWriter file = new FileWriter(filePath);
            file.write(jsonOrder.toString(3));
            file.close();
        } catch (IOException e) {
            System.out.println("JSON Order log write error: " + e.getMessage());
        }
    }

    private JSONObject createJSONOrderLog(Order order) {
        JSONObject jsonObject = new JSONObject();

        //add general info
        jsonObject.put("id", order.getId());
        jsonObject.put("status", order.getStatus());

        //add events
        jsonObject.put("events", order.getLog().getEvents());

        return jsonObject;
    }


}
