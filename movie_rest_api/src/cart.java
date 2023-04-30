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

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("sessionID", sessionId);
        responseJsonObject.addProperty("lastAccessTime", new Date(lastAccessTime).toString());

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

    private boolean delcontains(JsonArray previtems, JsonObject newrecord) {
        /* 
        for(int i = 0; i < previtems.size(); i++){
            if(item[0]["movieid"] == previtems[i])
        }
        */
        for (int i = 0; i < previtems.size(); i++) {
            if (previtems.get(i).getAsJsonObject().get("movieid").getAsString().equals(newrecord.get("movieid").getAsString())) {
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
                previousItems.add(newrecord);
                session.setAttribute("previousItems", previousItems);
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
                    if (!ifcontains(previousItems, newrecord)) {
                        previousItems.add(newrecord);
                        session.setAttribute("previousItems", previousItems);
                        responseJsonObject.addProperty("status", "success");
                    }
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
                    if (mincontains(previousItems, newrecord)) {
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
                if (delcontains(previousItems, newrecord)) {
                    session.setAttribute("previousItems", previousItems);
                    responseJsonObject.addProperty("status", "success");

                } else {
                    System.out.println("not exist");
                }
            } else {
                System.out.println("array empty");
            }

        }


        responseJsonObject.add("previousItems", previousItems);

        response.getWriter().write(responseJsonObject.toString());
    }
}