
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.ServletConfig;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.file.StandardOpenOption;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.*;
import java.util.HashMap;
import java.sql.PreparedStatement;
import java.io.*;
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



@WebServlet(name = "clearServlet", urlPatterns = "/clear")
public class clearcart extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
   

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
                HttpSession session = request.getSession();
                System.out.println("here3udeifienfiefemfemfemf");
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
                responseJsonObject.add("salesid",(JsonArray) session.getAttribute("salesid"));
                //session.setAttribute("previousItems", null);
                session.removeAttribute("previousItems");
                session.removeAttribute("salesid");
                System.out.println("removed");
        
                // write all the data into the jsonObject
                System.out.println(responseJsonObject.toString());
                response.getWriter().write(responseJsonObject.toString());
                //response.getWriter().write(session.getAttribute("salesid").toString());
        
       
    }
}
