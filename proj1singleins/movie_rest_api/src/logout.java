import java.io.IOException;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LogoutServlet", urlPatterns = "/api/logout")
public class logout extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        JsonObject responseJsonObject = new JsonObject();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "success");

        } else {
            responseJsonObject.addProperty("status", "failed");
            responseJsonObject.addProperty("message", "failed");
        }
        try {
            response.getWriter().write(responseJsonObject.toString());
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
