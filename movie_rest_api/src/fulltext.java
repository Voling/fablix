
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.io.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
@WebServlet(name = "fulltextServlet", urlPatterns = "/fulltextsearch")
public class fulltext extends HttpServlet{
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException {
        response.setContentType("application/json");
        String searched_text = request.getParameter("searchtext");
        PrintWriter out = response.getWriter();
        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT * FROM movies WHERE MATCH(title) AGAINST(?);";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, searched_text);
            System.out.println(statement.toString());
            ResultSet rs = statement.executeQuery();
            JsonArray jsonArray = new JsonArray();
            while(rs.next()){

                String title = rs.getString("title");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("title", title);
                jsonArray.add(jsonObject);

            }
            rs.close();
            statement.close();
            System.out.println(jsonArray);
            out.write(jsonArray.toString());
        }
        catch(Exception e) {
                // Write error message JSON object to output
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("errorMessage", e.getMessage());
                out.write(jsonObject.toString());
    
                // Log error to localhost log
                request.getServletContext().log("Error:", e);
                // Set response status to 500 (Internal Server Error)
                response.setStatus(500);
        }



    }
    
}
