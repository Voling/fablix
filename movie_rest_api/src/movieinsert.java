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
import java.util.Random;
@WebServlet(name = "movieinsert", urlPatterns = "/insertmovie")
public class movieinsert extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        String thestring = "";

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            thestring += characters.charAt(randomIndex);
        }

        return thestring;
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String title = request.getParameter("title");
        String theyear = request.getParameter("year");
        String director = request.getParameter("director");
        String star = request.getParameter("star");
        String genre = request.getParameter("genre");
        System.out.println(genre);
        //generate an Id of length 10
        String id = generateRandomString(10);
        JsonObject responseJsonObject = new JsonObject();
        if (title == null || title.equals("") || theyear == null || theyear.equals("") || star == null || star.equals("")|| genre == null || genre.equals("")){
            responseJsonObject.addProperty("status", "failed");
            responseJsonObject.addProperty("message ","null name");
            response.getWriter().write(responseJsonObject.toString());

            return;
        }
        int year = Integer.parseInt(theyear);
        

        //perform insertion
        try (Connection conn = dataSource.getConnection()) {
            //check if exist
            String query1 = "select * from movies where title = ?;";
            PreparedStatement statement1 = conn.prepareStatement(query1);
            statement1.setString(1, title);
            ResultSet rs = statement1.executeQuery();
            if (rs.next()){
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "movie already exists");
                statement1.close();
                conn.close();
                response.getWriter().write(responseJsonObject.toString());

                return;
            }
            statement1.close();
            
            String query = "CALL add_movie(?, ?,?,?,?,?);";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, id);
            statement.setString(2, title);
            statement.setInt(3, year);
            statement.setString(4, director);
            statement.setString(5, star);
            statement.setString(6, genre);
           

            System.out.println(statement.toString());
            boolean ifrs = statement.execute();
            if (ifrs){
                System.out.println(statement.getResultSet().toString());
            }
          

            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "Star inserted successfully");
            statement.close();
            conn.close();
            response.getWriter().write(responseJsonObject.toString());



        } catch (SQLException e) {
            e.printStackTrace();
            
           
            //JsonObject responseJsonObject = new JsonObject();
            //responseJsonObject.addProperty("message", "Star insertion failed");
        }
       



    }

}