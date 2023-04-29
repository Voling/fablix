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
import java.sql.Statement;
import java.sql.ResultSet;
import java.io.*;
import java.util.HashMap;
import java.sql.PreparedStatement;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "singlemovieroute", urlPatterns = "/api/single-movie")
public class singlemovieroute extends HttpServlet {
    private static final long serialVersionUID = 3L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"

            // Declare our statement
           String query = "SELECT\n" +
                   "    B.movieid,\n" +
                   "    B.title,\n" +
                   "    B.year,\n" +
                   "    B.director,\n" +
                   "    B.rating,\n" +
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
                   "            genres_in_movies.genreId,\n" +
                   "            stars_in_movies.starId\n" +
                   "        FROM\n" +
                   "            (\n" +
                   "                SELECT\n" +
                   "                    movies.id AS movieid,\n" +
                   "                    movies.title,\n" +
                   "                    movies.year,\n" +
                   "                    movies.director,\n" +
                   "                    ratings.rating\n" +
                   "                FROM\n" +
                   "                    movies\n" +
                   "                INNER JOIN ratings ON movies.id = ratings.movieId\n" +
                   "                WHERE movieid = ? "+
                   "            ) AS A\n" +
                   "        INNER JOIN genres_in_movies ON A.movieid = genres_in_movies.movieId\n" +
                   "        INNER JOIN stars_in_movies ON A.movieid = stars_in_movies.movieId\n" +
                   "    ) AS B\n" +
                   "INNER JOIN genres ON genres.id = B.genreId\n" +
                   "INNER JOIN stars ON stars.id = B.starId;";
            System.out.println(query);
            // Perform the query
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, id);
            ResultSet rs = statement.executeQuery();
            JsonObject jsonObject = new JsonObject();
            JsonArray jsonArray = new JsonArray();
            JsonArray starsInMovie = new JsonArray();

            JsonArray allGenres = new JsonArray();
            HashMap<String, Integer> genreTracker = new HashMap<>();

            boolean firstRun = false;

            // Iterate through each row of rs
            while (rs.next()) {

                if (!firstRun){
                    // put tile to json
                    String movieId = rs.getString("movieid");
                    String movieTitle = rs.getString("title");
                    String movieYear = rs.getString("year");
                    String movieDirector = rs.getString("director");
                    String movieRating = rs.getString("rating");
                    //String movieGenres = rs.getString("genrename");

                    // Create a JsonObject based on the data we retrieve from rs

                    jsonObject.addProperty("movie_id", movieId);
                    jsonObject.addProperty("movie_title", movieTitle);
                    jsonObject.addProperty("movie_year", movieYear);
                    jsonObject.addProperty("movie_director", movieDirector);
                    jsonObject.addProperty("movie_rating", movieRating);
                    //jsonObject.addProperty("movie_genres", movieGenres);
                    jsonObject.add("starsInMovie", starsInMovie); //create jsonarray in jsonobj

                    firstRun = true;
                    jsonArray.add(jsonObject);
                }
                //add all stars
                String starId = rs.getString("starId");
                String starName = rs.getString("starname");
                JsonObject combined = new JsonObject();
                combined.addProperty("starid",starId);
                combined.addProperty("starname",starName);
                starsInMovie.add(combined);

                String thisGenre = rs.getString("genrename");
                if (!genreTracker.containsKey(thisGenre)) {
                    genreTracker.put(thisGenre, 1);
                    allGenres.add(thisGenre);
                } // if not in hashmap (dne) then it can be added
            }
            jsonObject.add("movie_genres", allGenres);
            rs.close();
            statement.close();
            System.out.println(jsonArray);
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

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }

}
