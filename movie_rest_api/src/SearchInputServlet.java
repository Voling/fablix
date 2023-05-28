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
        try {
            
            
            //select * from movies inner join(select movieId FROM stars inner join stars_in_movies on stars.id = stars_in_movies.starId where stars.name like '%d%')as
            //A on movies.id = A.movieId;
            System.out.println("resquest gotten");            
            // Create a new connection to database
            Connection conn = dataSource.getConnection();
            // Declare a new statement
            String rawtitle = request.getParameter("title");
            //raw title parsed later
            String[] titlewords = rawtitle.split(" ");
            String parsedtitles = "";
            for (String word :titlewords){
                parsedtitles += "+" + word + "* ";
            }

            //System.out.println(title);
            String year = request.getParameter("year");
            String director = request.getParameter("director");
            String starname = request.getParameter("star");
            String page = request.getParameter("page");
            String pagesize = request.getParameter("pagesize");
            String sortmethod = request.getParameter("sort");
            String sortorder = request.getParameter("order");
            int psize = Integer.parseInt(pagesize);
            
            int pagenum;
            if(page == null){
                pagenum = 1;
            }
            else{
            pagenum = Integer.parseInt(page);
            }
            int offset = (pagenum-1)*psize;
            System.out.println("got here 1");  
            // Generate a SQL query
            String biggerquery =  "SELECT\n" +
            "    B.movieid,\n" +
            "    B.title,\n" +
            "    B.year,\n" +
            "    B.director,\n" +
            "    B.rating,\n" +
            "    genres.name AS genrename,\n" +
            "    stars.name AS starname,\n" +
            "    B.starId," +
            "    B.price \n" +
            "FROM\n" +
            "    (\n" +
            "        SELECT\n" +
            "            A.movieid,\n" +
            "            A.title,\n" +
            "            A.year,\n" +
            "            A.director,\n" +
            "            A.rating,\n" +
            "            A.price, \n" +
            "            genres_in_movies.genreId,\n" +
            "            stars_in_movies.starId\n" +
            "        FROM\n" +
            "            (\n" +
            "                SELECT\n" +
            "                    G.id AS movieid,\n" +
            "                    G.title,\n" +
            "                    G.year,\n" +
            "                    G.director,\n" +
            "                    G.price,\n      " +
            "                    ratings.rating\n" +
            "                FROM\n" +
            "                    ";
        String later = "";
        String query = "";
        int count = 1;
        int yearindex = -1;
        int directorindex = -1;
        int starindex = -1;
        int pageindex = -1;
        int pagesizeindex = -1;
        if(starname == null){
        later =       
            "                LEFT JOIN ratings ON G.id = ratings.movieId\n" +
            "                ORDER BY\n";
            if(sortmethod.equals("title")){
                later += " movies.title ";
            }
            else{
                later += " ratings.rating ";
            }
            if(sortorder.equals("ASC")){
                later += " ASC ";
            }
            else{
                later += " DESC ";
            }
            later +=
            "                LIMIT\n" +
            "                    ?\n" +
            "OFFSET ?"+
            "            ) AS A\n" +
            "        LEFT JOIN genres_in_movies ON A.movieid = genres_in_movies.movieId\n" +
            "        LEFT JOIN stars_in_movies ON A.movieid = stars_in_movies.movieId\n" +
            "    ) AS B\n" +
            "LEFT JOIN genres ON genres.id = B.genreId\n" +
            "LEFT JOIN stars ON stars.id = B.starId;";
       
            query = "(SELECT * FROM movies  WHERE 1=1 ";
            if (parsedtitles != null && !parsedtitles.equals("")) {query += " and MATCH(title) AGAINST(? in boolean mode ) or title like ? or edth(title,?,2) "; count +=3;}
            if (year != null ) {query += " and year =?"; yearindex = count;count += 1;}
            if (director != null&& !director.equals("")) {query += " and director like ?"; directorindex = count;count+=1;}
            query += " ) as G ";
            pagesizeindex = count;
            count += 1;
            pageindex = count;
           
            
           
            
        }
        else{
            later =       
            "                LEFT JOIN ratings ON G.id = ratings.movieId\n" +        
            "            ) AS A\n" +
            "        LEFT JOIN genres_in_movies ON A.movieid = genres_in_movies.movieId\n" +
            "        LEFT JOIN stars_in_movies ON A.movieid = stars_in_movies.movieId\n" +
            "    ) AS B\n" +
            "LEFT JOIN genres ON genres.id = B.genreId\n" +
            "LEFT JOIN stars ON stars.id = B.starId where stars.name like ?"+
            "                ORDER BY\n" +
            "                    -B.rating\n" +
            "                LIMIT\n" +
            "                    ?  OFFSET ?;" ;
             
            query = " WHERE 1=1";
            String query1 = 
            "SELECT \n"   +
            "T.movieid as movieid, \n"+
            "movies.title as title, \n"+
            "movies.year as year, \n" +
            "movies.director as director, \n" +
            "ratings.rating as rating, \n"+
            "genres.name AS genrename,\n" +
            "stars.name AS starname,\n" +
            "stars_in_movies.starId as starId,\n"+
            "movies.price as price \n"+
            "from" +
            "("+
            "        SELECT\n" +
            "             distinct A.movieid \n" +
            "        FROM\n" +
            "            (\n" +
            "                SELECT\n" +
            "                    movies.id AS movieid,\n" +
            "                    movies.title,\n" +
            "                    movies.year,\n" +
            "                    movies.director,\n" +
            "                    movies.price," +
            "                    ratings.rating\n" +
            "                FROM\n" +
            "                    movies\n"+

            "                LEFT JOIN ratings ON movies.id = ratings.movieId \n";
         
        String later1 =
            "            ) AS A\n" +
            "        LEFT JOIN stars_in_movies ON A.movieid = stars_in_movies.movieId\n" +
            " LEFT JOIN stars ON stars.id = stars_in_movies.starId\n" +
            " where stars.name like ?" +
            "                LIMIT\n" +
            "                    ?" +
            " OFFSET ?"+
            ") as T" +
            " LEFT JOIN stars_in_movies ON T.movieid = stars_in_movies.movieId\n" +
            " LEFT JOIN stars ON stars.id = stars_in_movies.starId \n"+
            " LEFT JOIN genres_in_movies ON T.movieid = genres_in_movies.movieId\n" +
            " LEFT JOIN genres ON genres.id = genres_in_movies.genreId\n" +
            " LEFT JOIN movies on T.movieid = movies.id\n" +
            " LEFT JOIN ratings ON T.movieid = ratings.movieId ";
        later1 +=  "                ORDER BY\n";
            if(sortmethod.equals("title")){
             later1 += " title ";
         }
         else{
             later1 += " rating ";
         }
         if(sortorder.equals("ASC")){
             later1 += " ASC ";
         }
         else{
             later1 += " DESC ";
         }
            
         later1 += ";";
            later = later1;
            biggerquery = query1;
            if (parsedtitles != null && !parsedtitles.equals("")) {query += " and MATCH(title) AGAINST(? in boolean mode ) or title like ? or edth(title,?,2)"; count +=3;}
            if (year != null ) {query += " and year =?"; yearindex = count;count += 1;}
            if (director != null&& !director.equals("")) {query += " and director like ?"; directorindex = count;count+=1;}
            starindex = count;
            count+=1;
            pagesizeindex = count;
            count += 1;
            pageindex = count;
        }
        PreparedStatement statement = conn.prepareStatement(biggerquery  + query + later);
        if(parsedtitles!= null && !parsedtitles.equals("")){
            statement.setString(1, parsedtitles);
            statement.setString(2,"%" +rawtitle + "%");
            statement.setString(3, rawtitle );
        }
        if(yearindex != -1){
            statement.setString(yearindex, year);
        }
        if(directorindex != -1){
            statement.setString(directorindex, director);
        }
        statement.setInt(pageindex, offset);
        statement.setInt(pagesizeindex,psize);
        if(starindex != -1){
            statement.setString(starindex, starname);
        }
            
            System.out.println(statement.toString());
            ResultSet rs = statement.executeQuery();
            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {
                String did = rs.getString("movieid");
                System.out.println(did);
                String dtitle = rs.getString("title");
                String dyear = rs.getString("year");
                String drating = rs.getString("rating");
                String ddirector = rs.getString("director");
                String dgenre = rs.getString("genrename");
                String dstar = rs.getString("starname");
                String dstarid = rs.getString("starId");
                String dprice = rs.getString("price");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", did);
                jsonObject.addProperty("title", dtitle);
                jsonObject.addProperty("year", dyear);
                jsonObject.addProperty("director", ddirector);
                jsonObject.addProperty("rating", drating);
                jsonObject.addProperty("genre", dgenre);
                jsonObject.addProperty("star", dstar);
                jsonObject.addProperty("starid", dstarid);
                jsonObject.addProperty("price",dprice);
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