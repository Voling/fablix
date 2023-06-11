
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
import java.io.*;
import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

@WebServlet(name = "Dashboard1Servlet", urlPatterns = "/_dashboard")
public class _dashboard extends HttpServlet {
    private DataSource dataSource;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedbReadOnly");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        ServletContext context = getServletContext();
        String loginHtmlPath = "/employeelogin.html";

        InputStream is = context.getResourceAsStream(loginHtmlPath);

        if (is != null) {
            response.setContentType("text/html");
            OutputStream os = response.getOutputStream();

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

            is.close();
            os.close();
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "File not found: " + loginHtmlPath);
        }
    }

    private String verifyemployee(String email, String password, Connection conn,
            PasswordEncryptor pencrypt) {

        String query = "select password from employees where email = ?;";
        String info = "error";
        try {
            String encryptedPassword = pencrypt.encryptPassword(password);

            System.out.println(encryptedPassword);
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {

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
            } else {
                return "notexist";
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
        JsonObject responseJsonObject = new JsonObject();
        String info = "";
        PasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
        try (Connection conn = dataSource.getConnection()) {


            info = verifyemployee(email, password, conn, passwordEncryptor);

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
            System.out.println("success!");
            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "success");

        } else {
            responseJsonObject.addProperty("status", "fail");
            request.getServletContext().log("Login failed");

            if (info.equals("notexist")) {
                responseJsonObject.addProperty("message", "user " + email + " doesn't exist");
            } else {
                responseJsonObject.addProperty("message", "incorrect password");
            }
        }
        System.out.println(responseJsonObject.toString());
        response.getWriter().write(responseJsonObject.toString());



    }

}


