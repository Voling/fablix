package edu.uci.ics.fabflixmobile.ui.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.webkit.CookieManager;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.databinding.ActivityLoginBinding;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;
import java.util.HashMap;
import java.util.Map;
import org.json.*;
import com.android.volley.AuthFailureError;
import java.util.Collections;


public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private TextView message;

    /*
      In Android, localhost is the address of the device or the emulator.
      To connect to your machine, you need to use the below IP address
     */
    private final String host = "10.0.2.2";
    private final String port = "8080";
    private final String domain = "fablix_war";
    private final String baseURL = "http://" + host + ":" + port + "/" + domain;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());

        username = binding.username;
        password = binding.password;
        message = binding.message;
        final Button loginButton = binding.login;
        Log.d("MyTag", baseURL);


        //assign a listener to call a function to handle the user request when clicking a button
        loginButton.setOnClickListener(view -> login());
    }

    @SuppressLint("SetTextI18n")
    public void login() {
        message.setText("Trying to login");
        Log.d("MyTag", baseURL);
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is POST
        final StringRequest loginRequest = new StringRequest(
                Request.Method.POST,
                baseURL + "/api/mobilelogin",
                response -> {
                    // TODO: should parse the json response to redirect to appropriate functions
                    //  upon different response value.
                    Log.d("login.success", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Log.d("ksnkndw",jsonObject.getString("status"));
                        if (jsonObject.getString("status").equals("success")){
                            Log.d("login.success", response);
                            //Complete and destroy login activity once successful
                            finish();
                            // initialize the activity(page)/destination
                            Intent MovieListPage = new Intent(LoginActivity.this, MovieListActivity.class);
                            // activate the list page.
                            startActivity(MovieListPage);
                        }
                        else{
                            Log.d("login.failed", response);
                            //show error messages
                            TextView tv = findViewById(R.id.message);
                            tv.setText("incorrect credential");

                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }

                },
                error -> {
                    // error
                    Log.d("login.error", error.toString());
                    TextView tv = findViewById(R.id.message);
                    tv.setText("incorrect credential");
                    tv.setVisibility(TextView.VISIBLE);
                }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse res){
                Map<String, String> responseHeaders = res.headers;

                String rawCookies = responseHeaders.get("Set-Cookie");
                if (rawCookies != null) {
                String[] cookieParts = rawCookies.split("=|;");
                String cookieName = cookieParts[0];
                String cookieValue = cookieParts[1];

                    CookieManager.getInstance().setCookie(cookieName, cookieValue);
                    Log.d(cookieName, cookieValue);
                }
                return super.parseNetworkResponse(res);
            }
            @Override
            protected Map<String, String> getParams() {
                // POST request form data
                final Map<String, String> params = new HashMap<>();
                params.put("email", username.getText().toString());
                params.put("password", password.getText().toString());
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                if (headers == null
                        || headers.equals(Collections.emptyMap())) {
                    headers = new HashMap<String, String>();
                }
                CookieManager cookieManager = CookieManager.getInstance();
                String cookies = cookieManager.getCookie(baseURL + "/api/mobilelogin");
                headers.put("Cookie", cookies);
                Log.d("cookies", "cookiesareput");
                return headers;
            }
        };
        // important: queue.add is where the login request is actually sent
        queue.add(loginRequest);
    }
}