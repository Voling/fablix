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
/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedbexample,
 * generates output as a html <table>
 */

// Declaring a WebServlet called FormServlet, which maps to url "/form"
@WebServlet(name = "SearchServlet", urlPatterns = "/search")
public class SearchInputServlet extends HttpServlet {
    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    // Use http GET

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("text/html");    // Response mime type
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        // Building page head with title
        out.println("Found Records");
        // Building page body
        out.println("Found Records");
        try {
            // Create a new connection to database
            Connection conn = dataSource.getConnection();
            // Declare a new statement
            //Statement statement = dbCon.createStatement();
            // Retrieve parameter "name" from the http request, which refers to the value of <input name="name"> in index.html
            String title = request.getParameter("title");
            System.out.println(title);
            String year = request.getParameter("year");
            String director = request.getParameter("director");
            //String starName = request.getParameter("star");
            // Generate a SQL query
            String query = "SELECT * FROM movies WHERE 1=1";
            if (title != null && !title.equals("")) {query += " and title like ?";}
            if (year != null ) {query += " and year =?";}
            if (director != null&& !director.equals("")) {query += " and director like ?";}
            System.out.println(query);
            PreparedStatement statement = conn.prepareStatement(query);
            // a decision tree on where to insert parameters
            if(title == null){
                if(year == null){
                    if (director == null){

                    }
                    else{
                        statement.setString(1, director);

                    }
                }
                else{
                    statement.setString(1, year);
                    if (director == null){

                    }
                    else{
                        statement.setString(2, director);

                    }

                }

            }
            else{
                statement.setString(1, title);
                if(year == null){
                    if (director == null){

                    }
                    else{
                        statement.setString(2, director);

                    }
                }
                else{
                    statement.setString(2, year);
                    if (director == null){

                    }
                    else{
                        statement.setString(3, director);

                    }


                }



            }
            System.out.println(statement.toString());
            ResultSet rs = statement.executeQuery();
            //if title, year, director present then add them to query
            //TODO: proper starName query addition
            //String getStar = String.format("SELECT id FROM stars WHERE stars.name = %s LEFT JOIN stars_in_movies ON stars_in_movies.starId = id", starName); //?
            //String abc = "SELECT * FROM movies WHERE ... RIGHT OUTER JOIN stars_in_movies ON movies.id = stars_in_movies.movieId AND ";
            //if (starName != null && !starName.equals("")) query += String.format("COMPLETED ADDITION GOES HERE", starName);
            //example

            //String query = String.format("SELECT * FROM movies WHERE name like '%s'", title); //queries name only

            //TODO: if star is active add to query...

            // Log to localhost log
            //request.getServletContext().log("query：" + query);

            // Perform the query
            //ResultSet rs = statement.executeQuery(query);
            // Create a html <table>
            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {
                String id = rs.getString("id");
                String dtitle = rs.getString("title");
                String dyear = rs.getString("year");
                String ddirector = rs.getString("director");
                //String genre = rs.getString("genrename");
                //String star = rs.getString("starname");
                //String starid = rs.getString("starId");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", id);
                jsonObject.addProperty("title", dtitle);
                jsonObject.addProperty("year", dyear);
                jsonObject.addProperty("director", ddirector);
                //jsonObject.addProperty("rating", rating);
                //jsonObject.addProperty("genre", genre);
                //jsonObject.addProperty("star", star);
                //jsonObject.addProperty("starid", starid);

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

        } catch (Exception e) {
            /*
             * After you deploy the WAR file through tomcat manager webpage,
             *   there's no console to see the print messages.
             * Tomcat append all the print messages to the file: tomcat_directory/logs/catalina.out
             *
             * To view the last n lines (for example, 100 lines) of messages you can use:
             *   tail -100 catalina.out
             * This can help you debug your program after deploying it on AWS.
             */
            request.getServletContext().log("Error: ", e);

            // Output Error Message to html
            out.println(String.format("<html><head><title>Error</title></head>\n<body><p>SQL error in doGet: %s</p></body></html>", e.getMessage()));
            return;
        }
        out.close();
    }
}