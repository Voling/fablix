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
import java.sql.Statement;

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
        out.println("<html><head><title>Found Records</title></head>");
        // Building page body
        out.println("<body><h1>Found Records</h1>");
        try {
            // Create a new connection to database
            Connection dbCon = dataSource.getConnection();
            // Declare a new statement
            Statement statement = dbCon.createStatement();
            // Retrieve parameter "name" from the http request, which refers to the value of <input name="name"> in index.html
            String title = request.getParameter("title");
            String year = request.getParameter("title");
            String director = request.getParameter("director");
            String starName = request.getParameter("star");
            // Generate a SQL query
            String query = "SELECT * FROM movies WHERE ";
            if (title != null && !title.equals("")) query += String.format("title like '%s' ", title);
            if (year != null && !year.equals("")) query += String.format(", year like '%s' ", year);
            if (director != null && !director.equals("")) query += String.format(", director like '%s' ", director);
            //if title, year, director present then add them to query
            //TODO: proper starName query addition
            //String getStar = String.format("SELECT id FROM stars WHERE stars.name = %s LEFT JOIN stars_in_movies ON stars_in_movies.starId = id", starName); //?
            //String abc = "SELECT * FROM movies WHERE ... RIGHT OUTER JOIN stars_in_movies ON movies.id = stars_in_movies.movieId AND ";
            //if (starName != null && !starName.equals("")) query += String.format("COMPLETED ADDITION GOES HERE", starName);
            //example

            //String query = String.format("SELECT * FROM movies WHERE name like '%s'", title); //queries name only

            //TODO: if star is active add to query...

            // Log to localhost log
            request.getServletContext().log("query：" + query);

            // Perform the query
            ResultSet rs = statement.executeQuery(query);
            // Create a html <table>
            out.println("<table border>");

            // Iterate through each row of rs and create a table row <tr>
            out.println("<tr><td>ID</td><td>Name</td></tr>");
            while (rs.next()) {
                String m_ID = rs.getString("ID");
                String m_Name = rs.getString("name");
                out.println(String.format("<tr><td>%s</td><td>%s</td></tr>", m_ID, m_Name));
            }
            out.println("</table>");

            // Close all structures
            rs.close();
            statement.close();
            dbCon.close();
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