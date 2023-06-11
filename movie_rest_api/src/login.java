import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import jakarta.servlet.ServletConfig;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;



@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class login extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedbReadOnly");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    private String verifycustomer(String email, String password, Connection conn,PasswordEncryptor pencrypt){

        String query = "select password from customers where email = ?;";
        String info = "error";
        try {
            String encryptedPassword = pencrypt.encryptPassword(password);
            System.out.println(encryptedPassword);
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            if (rs.next()){
            String supposedpw = rs.getString("password");
            System.out.println("password:");
            System.out.println(supposedpw);

            if (supposedpw == null) {
                info = "notexist";
                System.out.println("notexist");
                return info;
            }
            if (pencrypt.checkPassword(password, supposedpw)) {
                info = "found";
                return info;
            } else {
                return "incorrect";
            }
        }
        else{
            info = "notexist";
        }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return info;


    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("rjfbjfbrf");
        if (gRecaptchaResponse == null){
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "pls do recaptcha bro");
            response.getWriter().write(responseJsonObject.toString());
            return;
        }
        try { //captcha
            LoginRecaptcha.verify(gRecaptchaResponse);
        }
        catch (Exception e) {
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message","pls do recaptcha bro");
            response.getWriter().write(responseJsonObject.toString());
            return; //immediately fail post if captcha not done
        }
        PasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();


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
            //info = verifyuser(email, password, conn);
           
                info = verifycustomer(email, password, conn, passwordEncryptor);
            
            conn.close();
        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            System.out.println(jsonObject);
          

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
