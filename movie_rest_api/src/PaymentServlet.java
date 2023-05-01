import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import javax.naming.InitialContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import javax.sql.DataSource;

@WebServlet(name = "paymentServlet", urlPatterns = "/payment")
public class PaymentServlet extends HttpServlet {
    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String firstName = request.getParameter("firstname");
        String lastName = request.getParameter("lastname");
        String cardNumber = request.getParameter("cardnumber");
        String expiration = request.getParameter("expiration");
        expiration += "%";
        HttpSession session = request.getSession();
        JsonArray previousItems = (JsonArray) session.getAttribute("previousItems");

        // NEED TO RETRIEVE INFO FROM CART
        PrintWriter out = response.getWriter();

        try {
            Connection conn = dataSource.getConnection();
            user theuser = (user) (session.getAttribute("user"));
            String email = theuser.getemail();
            String ccquery =
                    "SELECT * FROM creditcards inner join customers on creditcards.id = customers.ccid WHERE "
                            + "creditcards.id = ? and\n" + "creditcards.firstName = ? and \n"
                            + "creditcards.lastName = ? and \n" + "creditcards.expiration like ? and \n"
                            + " customers.email = ?;";
            PreparedStatement statement = conn.prepareStatement(ccquery);
            statement.setString(1, cardNumber);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, expiration);
            statement.setString(5, email);
            System.out.println(statement.toString());
            // verify
            JsonObject responseJsonObject = new JsonObject();
            
            ResultSet rs = statement.executeQuery();
            if (rs.next()) { // credentials are correct;
                // HttpSession session = request.getSession();

                for (JsonElement movie : previousItems) {

                    String salesInsertion = "INSERT INTO sales (customerId,movieId , saleDate)\n"
                            + "VALUES (\n"
                            + "    (SELECT id FROM customers WHERE firstName = ? AND lastName = ? AND ccid = ?),\n"
                            + "    ?,\n" + "    CURRENT_TIMESTAMP\n" + ");";
                    // join customers with credit card info
                    int amount = movie.getAsJsonObject().get("amount").getAsInt();
                    String movieid = movie.getAsJsonObject().get("movieid").getAsString();
                    System.out.println(movieid);
                    for (int i = 0; i < amount; i++) {
                        PreparedStatement transactionTrack = conn.prepareStatement(salesInsertion);
                        transactionTrack.setString(1, firstName);
                        transactionTrack.setString(2, lastName);
                        transactionTrack.setString(3, cardNumber); // set all parameters
                        transactionTrack.setString(4, movieid);

                        transactionTrack.executeQuery();
                        transactionTrack.close();
                    }


                }


                // use email from session to fetch first,lastname,exp date from customer
                /* 
                String customerToCC =
                        "SELECT firstName, lastName, ccid, expiration FROM customer WHERE email = ?"
                                + "LEFT INNER JOIN creditcards "
                                + "ON customer.ccid = creditcards.id AND"
                                + " customer.firstName = creditcards.firstName AND"
                                + " customer.lastName = creditcards.lastName";
                PreparedStatement findCustomerWithCC = conn.prepareStatement(customerToCC);
                findCustomerWithCC.setString(1, email);
                ResultSet customerInfo = findCustomerWithCC.executeQuery();
                */

                responseJsonObject.addProperty("status", "success");
            }
            else{
                responseJsonObject.addProperty("status", "failure");
                System.out.println("failed");
            }
            
            conn.close();
            statement.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
