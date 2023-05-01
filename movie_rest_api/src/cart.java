import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.*;
import java.lang.Math.*;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "cartServlet", urlPatterns = "/cart")
public class cart extends HttpServlet {

    /**
     * handles GET requests to store session information
     */

    class purchaserecord {
        public String movieid;
        public int amount;
        public String title;
        public String director;
        public String year;

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        long lastAccessTime = session.getLastAccessedTime();
        float total = (float)session.getAttribute("total");

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("sessionID", sessionId);
        responseJsonObject.addProperty("lastAccessTime", new Date(lastAccessTime).toString());
        responseJsonObject.addProperty("total", total);

        JsonArray previousItems = (JsonArray) session.getAttribute("previousItems");
        if (previousItems == null) {
            previousItems = new JsonArray();
        }
        // Log to localhost log
        request.getServletContext().log("getting " + previousItems.size() + " items");
        responseJsonObject.add("previousItems", previousItems);

        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());
    }

    /**
     * handles POST requests to add and show the item list information
     */
    private boolean ifcontains(JsonArray previtems, JsonObject newrecord) {
        /* 
        for(int i = 0; i < previtems.size(); i++){
            if(item[0]["movieid"] == previtems[i])
        }
        */
        for (int i = 0; i < previtems.size(); i++) {
            if (previtems.get(i).getAsJsonObject().get("movieid").getAsString().equals(newrecord.get("movieid").getAsString())) {
                int original = previtems.get(i).getAsJsonObject().get("amount").getAsInt();
                previtems.get(i).getAsJsonObject().addProperty("amount", original + 1);
                return true;
            }
        }
        return false;
    }

    private boolean delcontains(JsonArray previtems, JsonObject newrecord, HttpSession session) {
        /* 
        for(int i = 0; i < previtems.size(); i++){
            if(item[0]["movieid"] == previtems[i])
        }
        */
        for (int i = 0; i < previtems.size(); i++) {
            if (previtems.get(i).getAsJsonObject().get("movieid").getAsString().equals(newrecord.get("movieid").getAsString())) {
                float lastprice = (float)session.getAttribute("total");
                float amount = (float)previtems.get(i).getAsJsonObject().get("amount").getAsInt();
                float theprice = previtems.get(i).getAsJsonObject().get("price").getAsFloat();
                System.out.println(amount);
                System.out.println("enjfnfkmf");
                session.setAttribute("total", lastprice-(amount*theprice));
                System.out.println(lastprice-(amount*theprice));

                previtems.remove(i);
            }
        }
        return false;
    }

    private boolean mincontains(JsonArray previtems, JsonObject newrecord) {
        /* 
        for(int i = 0; i < previtems.size(); i++){
            if(item[0]["movieid"] == previtems[i])
        }
        */
        for (int i = 0; i < previtems.size(); i++) {
            if (previtems.get(i).getAsJsonObject().get("movieid").getAsString().equals(newrecord.get("movieid").getAsString())) {
                int original = previtems.get(i).getAsJsonObject().get("amount").getAsInt();
                if (original > 0) {
                    previtems.get(i).getAsJsonObject().addProperty("amount", original - 1);
                }
                return true;
            }
        }
        return false;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String movieid = request.getParameter("movieid");
        String title = request.getParameter("title");
        String director = request.getParameter("director");
        String year = request.getParameter("year");
        String operation = request.getParameter("operation");
        String pricestr = request.getParameter("price");
        float price = Float.parseFloat(pricestr);
        HttpSession session = request.getSession();

        // get the previous items in a ArrayList
        JsonArray previousItems = (JsonArray) session.getAttribute("previousItems");
        JsonObject responseJsonObject = new JsonObject();
        if (operation.equals("add")) {
            if (previousItems == null) {
                previousItems = new JsonArray();
                JsonObject newrecord = new JsonObject();
                newrecord.addProperty("movieid", movieid);
                newrecord.addProperty("year", year);
                newrecord.addProperty("director", director);
                newrecord.addProperty("title", title);
                newrecord.addProperty("amount", 1);
                newrecord.addProperty("price", price);
                previousItems.add(newrecord);
                session.setAttribute("previousItems", previousItems);
                session.setAttribute("total", price);
                responseJsonObject.addProperty("status", "success");
            } else {
                // prevent corrupted states through sharing under multi-threads
                // will only be executed by one thread at a time
                synchronized (previousItems) {


                    JsonObject newrecord = new JsonObject();
                    newrecord.addProperty("movieid", movieid);
                    newrecord.addProperty("year", year);
                    newrecord.addProperty("director", director);
                    newrecord.addProperty("title", title);
                    newrecord.addProperty("amount", 1);
                    newrecord.addProperty("price", price);
                    float lastprice = (float)session.getAttribute("total");
                    session.setAttribute("total", lastprice+price);
                    if (!ifcontains(previousItems, newrecord)) {
                        previousItems.add(newrecord);
                        session.setAttribute("previousItems", previousItems);
                        
                    }
                    responseJsonObject.addProperty("status", "success");

                }
            }
        }
        if (operation.equals("minus")) {
            if (previousItems != null) {
                synchronized (previousItems) {
                    JsonObject newrecord = new JsonObject();
                    newrecord.addProperty("movieid", movieid);
                    newrecord.addProperty("year", year);
                    newrecord.addProperty("director", director);
                    newrecord.addProperty("title", title);
                    newrecord.addProperty("amount", 1);
                    newrecord.addProperty("price", price);
                    if (mincontains(previousItems, newrecord)) {
                        float lastprice = (float)session.getAttribute("total");
                        session.setAttribute("total", lastprice - price);
                        session.setAttribute("previousItems", previousItems);
                        responseJsonObject.addProperty("status", "success");

                    } else {
                        System.out.println("not exist");
                    }

                }
            } else {
                System.out.println("array empty");
            }
        }
        if (operation.equals("remove")) {
            if (previousItems != null) {
                JsonObject newrecord = new JsonObject();
                newrecord.addProperty("movieid", movieid);
                newrecord.addProperty("year", year);
                newrecord.addProperty("director", director);
                newrecord.addProperty("title", title);
                newrecord.addProperty("amount", 1);
                newrecord.addProperty("price", price);
                if (delcontains(previousItems, newrecord,session)) {
                    
                    session.setAttribute("previousItems", previousItems);
                    responseJsonObject.addProperty("status", "success");

                } else {
                    System.out.println("not exist");
                }
            } else {
                System.out.println("array empty");
            }

        }
        float lastprice = (float)session.getAttribute("total");
        System.out.print(lastprice);
        int decimalPlaces = 3;

        double factor = Math.pow(10, decimalPlaces);
        double tempValue = lastprice * factor;
        long roundedTempValue = Math.round(tempValue);
        float roundedValue = (float) (roundedTempValue / factor);

        responseJsonObject.addProperty("total",roundedValue);

        responseJsonObject.add("previousItems", previousItems);

        response.getWriter().write(responseJsonObject.toString());
    }
}