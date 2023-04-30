import com.google.gson.JsonObject;
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
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.*;
import java.util.HashMap;
import java.sql.PreparedStatement;
import java.io.*;



@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class login extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private String hash(String astring) {
        String hashed = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedpassword = digest.digest(astring.getBytes(StandardCharsets.UTF_8));
            for (byte b : hashedpassword) {
                // hpassword.append(String.format("%02x", b));
                hashed += String.format("%02x", b);
            }
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error hashing password: " + e.getMessage());
        }
        return hashed;


    }

    private String verifyuser(String email, String password, Connection conn) {
        // String matched = "";
        String info = "error";

        // hashing the email
        String hashedemail = hash(email);
        String hashedpassword = hash(password);
        System.out.println(hashedpassword);
        String query = "select password from user where email = ?;";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, hashedemail);
            ResultSet rs = statement.executeQuery();
            rs.next();
            String supposedpw = rs.getString("password");
            System.out.println("password:");
            System.out.println(supposedpw);
            if (supposedpw == null) {
                info = "notexist";
                System.out.println("notexist");
                return info;
            }
            if (hashedpassword.equals(supposedpw)) {
                info = "found";
                return info;
            } else {
                return "incorrect";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }



        return info;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        System.out.println(email);
        System.out.println(password);

        /*
         * This example only allows username/password to be test/test / in the real project, you
         * should talk to the database to verify username/password
         */
        JsonObject responseJsonObject = new JsonObject();
        String info = "";
        try (Connection conn = dataSource.getConnection()) {
            info = verifyuser(email, password, conn);
            conn.close();
        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            System.out.println(jsonObject);
            // out.write(jsonObject.toString());

            // Log error to localhost log
            // request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            // response.setStatus(500);

        }
        if (info.equals("found")) {
            // Login success:
            // set this user into the session
            request.getSession().setAttribute("user", new user(email));
            System.out.println("sucess!");
            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "success");

        } else {
            // Login fail
            responseJsonObject.addProperty("status", "fail");
            // Log to localhost log
            request.getServletContext().log("Login failed");
            // sample error messages. in practice, it is not a good idea to tell user which one is
            // incorrect/not exist.
            if (info.equals("notexist")) {
                responseJsonObject.addProperty("message", "user " + email + " doesn't exist");
            } else {
                responseJsonObject.addProperty("message", "incorrect password");
            }
        }
        response.getWriter().write(responseJsonObject.toString());
       
    }
}
