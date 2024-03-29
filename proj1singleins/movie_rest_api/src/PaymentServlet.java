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
import javax.naming.InitialContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import javax.sql.DataSource;
import java.sql.Statement;

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
            JsonArray jsonArray = new JsonArray();

            if (rs.next()) { // credentials are correct;
            
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
                        PreparedStatement transactionTrack = conn.prepareStatement(salesInsertion, Statement.RETURN_GENERATED_KEYS);
                        transactionTrack.setString(1, firstName);
                        transactionTrack.setString(2, lastName);
                        transactionTrack.setString(3, cardNumber); // set all parameters
                        transactionTrack.setString(4, movieid);

                        transactionTrack.executeUpdate();
                        ResultSet rs1 = transactionTrack.getGeneratedKeys();
                       
                        while(rs1.next()){
                            int salesId = rs1.getInt(1);
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("id", salesId);
                            jsonArray.add(jsonObject);

                        }
                        //
                        transactionTrack.close();
                    }


                }
                

                responseJsonObject.addProperty("status", "success");
                session.setAttribute("salesid", jsonArray);
            }
            else{
                responseJsonObject.addProperty("status", "failure");
                System.out.println("failed");
            }
            
            conn.close();
            statement.close();
            response.getWriter().write(responseJsonObject.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
