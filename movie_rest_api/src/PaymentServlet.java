import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;

@WebServlet(name= "paymentServlet", urlPatterns="/payment")
public class PaymentServlet extends HttpServlet {
    private DataSource dataSource;
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String cardNumber = request.getParameter("cardNumber");
        String expiration = request.getParameter("expiration");
        String[] moviesInCart = request.getParameterValues("cartMovieIDs");
        //NEED TO RETRIEVE INFO FROM CART
        PrintWriter out = response.getWriter();

        try {
            Connection conn = dataSource.getConnection();
            String ccquery = "SELECT * FROM creditcards WHERE" +
                    "? = id" +
                    "? = firstName" +
                    "? = lastName" +
                    "? = expiration";
            PreparedStatement statement = conn.prepareStatement(ccquery);
            statement.setString(1, cardNumber);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, expiration);
            //verify
            ResultSet rs = statement.executeQuery();
            if (rs.next()) { //credentials are correct;
                HttpSession session = request.getSession();
                JsonArray allCartItems = (JsonArray)session.getAttribute("previousItems");

                String salesInsertion = "INSERT INTO sales (customerid, movieid, saledate)\n" +
                        "VALUES (\n" +
                        "    (SELECT id FROM customers WHERE firstName = ? AND lastName = ? AND ccid = ?),\n" +
                        "    ?,\n" +
                        "    CURRENT_TIMESTAMP\n" +
                        ");";
                //join customers with credit card info
                PreparedStatement transactionTrack = conn.prepareStatement(salesInsertion);

                transactionTrack.setString(1, firstName);
                transactionTrack.setString(2, lastName);
                transactionTrack.setString(3, cardNumber);
                transactionTrack.setString(4, "");
                //use email from session to fetch first,lastname,exp date from customer
                String customerToCC = "SELECT firstName, lastName, ccid, expiration FROM customer WHERE email = ?" +
                        "LEFT INNER JOIN creditcards " +
                            "ON customer.ccid = creditcards.id AND" +
                            " customer.firstName = creditcards.firstName AND" +
                            " customer.lastName = creditcards.lastName";
                PreparedStatement findCCwithCustomer = conn.prepareStatement(customerToCC);

                transactionTrack.close();
            }


            conn.close();
            statement.close();

        }
        catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
