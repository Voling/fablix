import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;

@WebServlet(name= "paymentServlet", urlPatterns="/paymentInfo")
public class PaymentServlet extends HttpServlet {
    private DataSource dataSource;
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String cardNumber = request.getParameter("cardNumber");
        String expiration = request.getParameter("expiration");
        PrintWriter out = response.getWriter();
        boolean validCredentials = false;
        try {
            Connection conn = dataSource.getConnection();
            String ccquery = "SELECT id, firstName, lastName, expiration FROM creditcards WHERE" +
                    "? == id" +
                    "? == firstName" +
                    "? = lastName" +
                    "? = expiration";
            PreparedStatement statement = conn.prepareStatement(ccquery);
            statement.setString(1, cardNumber);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, expiration);

            ResultSet rs = statement.executeQuery();
            int rowsAffected = statement.executeUpdate();

            validCredentials = rs.next(); //credentials are correct
            conn.close();

        }
        catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
