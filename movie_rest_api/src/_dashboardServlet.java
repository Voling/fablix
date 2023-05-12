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


@WebServlet(name = "DashboardServlet", urlPatterns = "/api/_dashboard")
public class _dashboardServlet extends HttpServlet {
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
        if (request.getSession().getAttribute("user") != null) { //check user logged in
            String action = request.getParameter("action");

            if (action != null) {
                if (action.equals("insertStar")) {
                    insertStar(request, response);
                } else if (action.equals("getMetadata")) {
                    getMetadata(response);
                } else {
                    responseJsonObject.addProperty("error", "No action specified");
                    response.getWriter().write(responseJsonObject.toString());
                }
            } else {
                responseJsonObject.addProperty("error", "No action specified");
                response.getWriter().write(responseJsonObject.toString());
            } //
        } else {
            responseJsonObject.addProperty("error", "User not logged in");
            response.getWriter().write(responseJsonObject.toString());
        } //user not logged in error
    }
    private void insertStar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String starName = request.getParameter("name");
        String birthYear = request.getParameter("birthYear");

        //require star name
        if (starName == null || starName.isEmpty()) {
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("error", "Star name required");
            response.getWriter().write(responseJsonObject.toString());
            return;
        }

        //perform insertion
        try (Connection conn = dataSource.getConnection()) {
            String query = "INSERT INTO stars (name, birthYear) VALUES (?, ?)";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, starName);

            if (birthYear != null && !birthYear.isEmpty()) {
                statement.setInt(2, Integer.parseInt(birthYear));
            } else {
                statement.setNull(2, java.sql.Types.INTEGER);
            }

            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("message", "Star inserted successfully");

            response.getWriter().write(responseJsonObject.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            //JsonObject responseJsonObject = new JsonObject();
            //responseJsonObject.addProperty("message", "Star insertion failed");
        }

    }
    private void getMetadata(HttpServletResponse response) throws IOException {
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