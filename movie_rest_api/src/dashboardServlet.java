import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.*;
import java.util.HashMap;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


@WebServlet(name = "DashboardServlet", urlPatterns = "/dashboard")
public class dashboardServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject responseJsonObject = new JsonObject();
       
            System.out.println("herehere");
          
                    insertStar(request, response,responseJsonObject);
                   
                   
                    response.getWriter().write(responseJsonObject.toString());

               
          
        
    }
    private void insertStar(HttpServletRequest request, HttpServletResponse response,JsonObject responseJsonObject) throws IOException {
        String starName = request.getParameter("name");
        String birthYear = request.getParameter("birthYear");
        System.out.println(starName);
        System.out.println(birthYear);

        //require star name
        if (starName == null || starName.equals("")) {
            responseJsonObject.addProperty("status", "failed");
            responseJsonObject.addProperty("message ","null name");
            return;
        }

        //perform insertion
        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT add_star(?, ?);";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, starName);

            if (birthYear != null && !birthYear.equals("")) {
                statement.setInt(2, Integer.parseInt(birthYear));
            } else {
                statement.setNull(2, java.sql.Types.INTEGER);
            }
            System.out.println(statement.toString());
            boolean ifrs = statement.execute();
            if (ifrs){
                System.out.println(statement.getResultSet().toString());
            }
          

            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "Star inserted successfully");
            statement.close();
            conn.close();


        } catch (SQLException e) {
            e.printStackTrace();
            
           
            //JsonObject responseJsonObject = new JsonObject();
            //responseJsonObject.addProperty("message", "Star insertion failed");
        }
       

    }
    protected void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException {
        // Retrieve metadata from the database
        String metadataQuery = "SHOW TABLES";

        try (Connection conn = dataSource.getConnection()) {
            // Get table names
            PreparedStatement statement = conn.prepareStatement(metadataQuery);
            ResultSet tablesResult = statement.executeQuery();
            JsonArray tablesArray = new JsonArray();
            while (tablesResult.next()) {
                String tableName = tablesResult.getString(1);
                tablesArray.add(tableName);
            }

            JsonObject metadataJson = new JsonObject();

            for (int i = 0; i < tablesArray.size(); i++) {
                String tableName = tablesArray.get(i).getAsString();
                //get each tableName in db
                ResultSet columnsResult = conn.getMetaData().getColumns(null, null, tableName, null);
                JsonArray columnsArray = new JsonArray();

                while (columnsResult.next()) {
                    //get all details of each table
                    String columnName = columnsResult.getString("COLUMN_NAME");
                    String columnType = columnsResult.getString("TYPE_NAME");

                    JsonObject columnJson = new JsonObject();
                    columnJson.addProperty("name", columnName);
                    columnJson.addProperty("type", columnType);

                    columnsArray.add(columnJson);
                }
                metadataJson.add(tableName, columnsArray);
            }

            // Send the metadata as a JSON response
            response.getWriter().write(metadataJson.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}