
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.google.gson.JsonArray;
import jakarta.servlet.http.HttpSession;
import java.util.Date;



@WebServlet(name = "clearServlet", urlPatterns = "/clear")
public class clearcart extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */


    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        System.out.println("here3udeifienfiefemfemfemf");
        String sessionId = session.getId();
        long lastAccessTime = session.getLastAccessedTime();
        float total = (float) session.getAttribute("total");

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("sessionID", sessionId);
        responseJsonObject.addProperty("lastAccessTime", new Date(lastAccessTime).toString());
        responseJsonObject.addProperty("total", total);

        JsonArray previousItems = (JsonArray) session.getAttribute("previousItems");
        if (previousItems == null) {
            previousItems = new JsonArray();
        }
        // Log to localhost log
        request.getServletContext().log("getting " + previousItems.size() + " items");
        responseJsonObject.add("previousItems", previousItems);
        responseJsonObject.add("salesid", (JsonArray) session.getAttribute("salesid"));
        session.removeAttribute("previousItems");
        session.removeAttribute("salesid");
        System.out.println("removed");

        // write all the data into the jsonObject
        System.out.println(responseJsonObject.toString());
        response.getWriter().write(responseJsonObject.toString());



    }
}
