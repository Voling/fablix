import jakarta.servlet.http.HttpServlet;

import java.io.IOException;
import java.util.HashMap;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import netscape.javascript.JSObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mysql.cj.xdevapi.PreparableStatement;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.naming.InitialContext;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
// this endpoint is like search but only returns movie id and title 

@WebServlet(name = "autosuggest", urlPatterns = "/autosuggest")
public class autosuggest extends HttpServlet{

     private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException {
        response.setContentType("application/json"); // Response mime type
         String text = request.getParameter("text");
         String[] titlewords = text.split(" ");
            String parsedtitles = "";
            for (String word :titlewords){
                parsedtitles += "+" + word + "* ";
            }

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            String query = "select title,id from movies where MATCH(title) AGAINST(? in boolean mode ) LIMIT 10;";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1,parsedtitles);
            ResultSet rs = statement.executeQuery();
            JsonArray jsonArray = new JsonArray();
            while(rs.next()){
                String id = rs.getString("id");
                String title = rs.getString("title");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("data", id);
                jsonObject.addProperty("value", title);
                jsonArray.add(jsonObject);
                 


            }
            rs.close();
            statement.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);
            conn.close();


        }
        catch (Exception e) {
            e.printStackTrace();
        }




    }
    
    }
