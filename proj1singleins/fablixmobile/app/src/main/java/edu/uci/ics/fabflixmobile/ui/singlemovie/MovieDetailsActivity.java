package edu.uci.ics.fabflixmobile.ui.singlemovie;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details_page);

        if (getIntent().hasExtra("movie")) {
            Movie movie = (Movie) getIntent().getSerializableExtra("movie");
            Log.d("MovieDetailsPage", movie.getName());

            TextView titleTextView = findViewById(R.id.title);
            TextView yearTextView = findViewById(R.id.year);
            TextView directorTextView = findViewById(R.id.director);
            TextView genresTextView = findViewById(R.id.genres);
            TextView starsTextView = findViewById(R.id.stars);

            titleTextView.setText(movie.getName());
            yearTextView.setText(String.valueOf(movie.getYear()));
            directorTextView.setText(movie.getDirector());

            StringBuilder allGenres = new StringBuilder();
            for (String i : movie.getGenres()) {
                allGenres.append(i).append(", ");
            }
            genresTextView.setText(String.valueOf(allGenres));

            StringBuilder allStars = new StringBuilder();
            for (String i : movie.getStars()) {
                allStars.append(i).append(", ");
            }
            starsTextView.setText(String.valueOf(allStars));
        }
        Button backButton = findViewById(R.id.prevButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });

    }


}