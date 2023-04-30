import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mysql.cj.xdevapi.Statement;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import javax.sql.DataSource;
import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServlet;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet(name="BrowseServlet", urlPatterns="/browse")
public class BrowseServlet extends HttpServlet {
    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String browseType = request.getParameter("type"); //movie/title
        String browseTerm = request.getParameter("term"); //letter if title,
        String pagesize = request.getParameter("pagesize");
        int psize = Integer.parseInt(pagesize);
        String page = request.getParameter("page");
            int pagenum;
            if(page == null){
                pagenum = 1;
            }
            else{
            pagenum = Integer.parseInt(page);
            }
            int offset = (pagenum-1)*psize;
            ResultSet rs;
             JsonArray result = new JsonArray();

        //word if genre
        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement statement = conn.prepareStatement("select * from movies;");
            if (browseType.equals("title")) {
                rs = browseByTitle(browseTerm,offset, conn, statement,psize);

            } //browsing by title
            else {
                rs = browseByGenre(browseTerm,offset, conn,statement,psize);
            }
            System.out.println(statement.toString());
            //browsing by genre
            while (rs.next()) {
                String did = rs.getString("T.movieid");
                String dtitle = rs.getString("T.title");
                String dyear = rs.getString("T.year");
                String ddirector = rs.getString("T.director");
                String drating = rs.getString("T.rating");
                String dgenre = rs.getString("genres.name");
                String dstar = rs.getString("stars.name");
                String dstarid = rs.getString("stars.id");


                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", did);
                jsonObject.addProperty("title", dtitle);
                jsonObject.addProperty("year", dyear);
                jsonObject.addProperty("director", ddirector);
                jsonObject.addProperty("rating", drating);
                jsonObject.addProperty("genre", dgenre);
                jsonObject.addProperty("star", dstar);
                jsonObject.addProperty("starid", dstarid);


                result.add(jsonObject);
            }
            rs.close();
            //statement closed in helpers
            request.getServletContext().log("getting " + result.size() + " results");
            out.write(result.toString());
            statement.close();
            conn.close();
        }
        catch (Exception e){
            request.getServletContext().log("Error: ", e);
            return;
        }
        out.close();
        response.setStatus(200);
    }
    private ResultSet browseByGenre(String browseTerm, int offset, Connection conn, PreparedStatement statement,int psize) throws ServletException {
        //<String> movieList = new ArrayList<>();
        String query = 
        "SELECT * "   +
      
        "from" +
        "("+
        "        SELECT\n" +
        "            A.movieid,\n" +
        "            A.title,\n" +
        "            A.year,\n" +
        "            A.director,\n" +
        "            A.rating\n" +
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
        "                ORDER BY\n" +
        "                    -ratings.rating\n" +
        "            ) AS A\n" +
        "        INNER JOIN genres_in_movies ON A.movieid = genres_in_movies.movieId\n" +
       // "        INNER JOIN stars_in_movies ON A.movieid = stars_in_movies.movieId\n" +
        " INNER JOIN genres ON genres.id = genres_in_movies.genreId\n" +
        //"INNER JOIN stars ON stars.id = B.starId" +
        " where genres.name = ?" +
        "                LIMIT\n" +
        "                    ?" +
        " OFFSET ?"+
        ") as T" +
        " INNER JOIN stars_in_movies ON T.movieid = stars_in_movies.movieId\n" +
        " INNER JOIN stars ON stars.id = stars_in_movies.starId"+
        " INNER JOIN genres_in_movies ON T.movieid = genres_in_movies.movieId\n" +
        " INNER JOIN genres ON genres.id = genres_in_movies.genreId;";

       
        //Connection conn;
        try {
            
            statement = conn.prepareStatement(query);
            statement.setInt(3, offset);
            statement.setInt(2, psize);
            statement.setString(1,browseTerm);
            System.out.println(statement.toString());
            ResultSet rs = statement.executeQuery();
            //statement.close();
            //conn.close();
            return rs;
        } catch (SQLException e) {
            throw new ServletException("Error retrieving movies by genre", e);
        }


    }
    private ResultSet browseByTitle(String browseTerm, int offset, Connection conn, PreparedStatement statement,int psize) throws ServletException {
        //ArrayList<String> genreList = new ArrayList<>();
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
        "where movies.title like ?"+
        "                ORDER BY\n" +
        "                    -ratings.rating\n" +
        "                LIMIT\n" +
        "                    ?\n" +
        "OFFSET ?"+
        "            ) AS A\n" +
        "        INNER JOIN genres_in_movies ON A.movieid = genres_in_movies.movieId\n" +
        "        INNER JOIN stars_in_movies ON A.movieid = stars_in_movies.movieId\n" +
        "    ) AS B\n" +
        "INNER JOIN genres ON genres.id = B.genreId\n" +
        "INNER JOIN stars ON stars.id = B.starId;";
        try {
            statement = conn.prepareStatement(query);
            statement.setInt(3, offset);
            statement.setInt(2,psize);
            statement.setString(1,browseTerm);
            ResultSet rs = statement.executeQuery();
            //statement.close();
            //conn.close();
            return rs;
        } catch (SQLException e) {
            throw new ServletException("Error retrieving movies by title.", e);
        }


    }
}