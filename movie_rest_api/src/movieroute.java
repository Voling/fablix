import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "movieroute", urlPatterns = "/api/movies")
public class movieroute extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedbReadOnly");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type
        System.out.println("knkrgrgrggr");
        String page = request.getParameter("page");
        String pagesize = request.getParameter("pagesize");
        String sortmethod = request.getParameter("sort");
        String sortorder = request.getParameter("order");
        int pagenum = Integer.parseInt(page);
        int psize = Integer.parseInt(pagesize);
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
           
            String query = "SELECT\n" +
                    "    B.movieid,\n" +
                    "    B.title,\n" +
                    "    B.year,\n" +
                    "    B.director,\n" +
                    "    B.rating,\n" +
                    "    B.price," +
                    "    genres.name AS genrename,\n" +
                    "    stars.name AS starname,\n" +
                    "    B.starId " +
                    "FROM\n" +
                    "    (\n" +
                    "        SELECT\n" +
                    "            A.movieid,\n" +
                    "            A.title,\n" +
                    "            A.year,\n" +
                    "            A.director,\n" +
                    "            A.rating,\n" +
                    "            A.price," +
                    "            genres_in_movies.genreId,\n" +
                    "            stars_in_movies.starId\n" +
                    "        FROM\n" +
                    "            (\n" +
                    "                SELECT\n" +
                    "                    movies.id AS movieid,\n" +
                    "                    movies.title,\n" +
                    "                    movies.year,\n" +
                    "                    movies.director,\n" +
                    "                    movies.price,"+
                    "                    ratings.rating\n" +
                    "                FROM\n" +
                    "                    movies\n" +
                    "                INNER JOIN ratings ON movies.id = ratings.movieId\n" +
                    "                ORDER BY\n";
                    if(sortmethod.equals("title")){
                        query += " movies.title ";
                    }
                    else{
                        query += " ratings.rating ";
                    }
                    if(sortorder.equals("ASC")){
                        query += " ASC ";
                    }
                    else{
                        query += " DESC ";
                    }
                    //"                    -ratings.rating\n" +
                   query += "                LIMIT\n" +
                    "                    ?\n" +
                    "OFFSET ?"+
                    "            ) AS A\n" +
                    "        INNER JOIN genres_in_movies ON A.movieid = genres_in_movies.movieId\n" +
                    "        INNER JOIN stars_in_movies ON A.movieid = stars_in_movies.movieId\n" +
                    "    ) AS B\n" +
                    "INNER JOIN genres ON genres.id = B.genreId\n" +
                    "INNER JOIN stars ON stars.id = B.starId;";

            PreparedStatement statement = conn.prepareStatement(query);
            int offset = (pagenum-1)*20;
            statement.setInt(1,psize);
            statement.setInt(2, offset);

            System.out.println(statement.toString());
            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String id = rs.getString("movieid");
                String title = rs.getString("title");
                String year = rs.getString("year");
                String rating = rs.getString("rating");
                String director = rs.getString("director");
                String genre = rs.getString("genrename");
                String star = rs.getString("starname");
                String starid = rs.getString("starId");
                String price = rs.getString("price");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", id);
                jsonObject.addProperty("title", title);
                jsonObject.addProperty("year", year);
                jsonObject.addProperty("director", director);
                jsonObject.addProperty("rating", rating);
                jsonObject.addProperty("genre", genre);
                jsonObject.addProperty("star", star);
                jsonObject.addProperty("starid", starid);
                jsonObject.addProperty("price", price);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");
            System.out.println(jsonArray.toString());
            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);
            conn.close();

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }
}
