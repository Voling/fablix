package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import android.widget.Button;

import com.android.volley.Response;
import android.os.Bundle;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;
import edu.uci.ics.fabflixmobile.ui.singlemovie.MovieDetailsActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import java.util.ArrayList;
import android.view.View;
import androidx.appcompat.widget.SearchView;
import edu.uci.ics.fabflixmobile.R;
import android.content.Context;

public class MovieListActivity extends AppCompatActivity {
    private final String host = "10.0.2.2";
    private final String port = "8080";
    private final String domain = "fablix_war";
    private final String baseURL = "http://" + host + ":" + port + "/" + domain;
    private ArrayList<Movie> movies;
    private MovieListViewAdapter movieadapter;
    private int pagenum = 1;
    private String lastquery = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);
        // TODO: this should be retrieved from the backend server
        //final ArrayList<Movie> movies = new ArrayList<>();
        Log.d("wjbdj",baseURL + "/api/movies");
        movies = getMovieList();
        //movies.add(new Movie("The Terminal", (short) 2004));
        //movies.add(new Movie("The Final Season", (short) 2007));
        /*
        MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Movie movie = movies.get(position);
            @SuppressLint("DefaultLocale") String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getName(), movie.getYear());
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            Intent MovieDetailsPage = new Intent(MovieListActivity.this, MovieDetailsActivity.class);
            //go to movie page
            MovieDetailsPage.putExtra("movie", movie);
            startActivity(MovieDetailsPage);
        });

         */
    }
    protected void parseMovie(ArrayList<Movie> movieList, String response){

    try {
        JSONArray jsonArray = new JSONArray(response);
        String lastmovie = "";
        String prevstar = "";
        String prevgenre = "";
        int length = -1;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (!jsonObject.getString("title").equals(lastmovie)) {
                String title = jsonObject.getString("title");
                Log.d("title", title);
                Log.d("lastmov", lastmovie);
                int year = jsonObject.getInt("year");
                String director = jsonObject.getString("director");
                ArrayList<String> starList = new ArrayList<String>();
                starList.add(jsonObject.getString("star"));
                ArrayList<String> genreList = new ArrayList<String>();
                genreList.add(jsonObject.getString("genre"));
                movieList.add(new Movie(title, year, director, genreList, starList));
                // update prior
                lastmovie = jsonObject.getString("title");
                prevgenre = jsonObject.getString("genre");
                prevstar = jsonObject.getString("star");
                length += 1;
            } else {
                                /*
                                if (resultdata[i].genre != prevgenre) {
                                    array[length].genres.push(resultdata[i].genre);
                                    prevgenre = resultdata[i].genre;
                                   }
                                if (resultdata[i].star != prevstar) {
                                    array[length].cast.push([resultdata[i].star, resultdata[i].starid]);
                                    prevstar = resultdata[i].star;
                                    //console.log(resultdata[i].starid);
                                    }
                                 */
                if (!jsonObject.getString("genre").equals(prevgenre)) {
                    movieList.get(length).getGenres().add(jsonObject.getString("genre"));
                    prevgenre = jsonObject.getString("genre");
                }
                if (!jsonObject.getString("star").equals(prevstar)) {
                    movieList.get(length).getStars().add(jsonObject.getString("star"));
                    prevstar = jsonObject.getString("star");
                }


            }
        }
    }
    catch(Exception e){
        e.printStackTrace();
    }
    }
    protected void getlast(){
        ArrayList<Movie> movieList = new ArrayList<>();
        if(pagenum >=2) {
            pagenum -= 1;
            Log.d("wdbwjdb", baseURL + lastquery + "&page=" + pagenum);
            final RequestQueue queue = NetworkManager.sharedManager(this).queue;
            final StringRequest movieListRequest = new StringRequest(
                    Request.Method.GET,
                    baseURL + lastquery + "&page=" + pagenum,
                    response -> {
                        parseMovie(movieList, response);
                        Log.d("searchedmovie", movieList.toString());
                        movies.clear();
                        movies.addAll(movieList);
                        movieadapter.notifyDataSetChanged();
                        Log.d("updatedmovies", movies.toString());
                        // Handle response
                    },
                    error -> {
                        // Handle error
                        Log.e("Volley", "Error occurred", error);
                    }
            );
            queue.add(movieListRequest);
        }
    }
    protected void getnext(){
        ArrayList<Movie> movieList = new ArrayList<>();
        pagenum += 1;
        Log.d("wdbwjdb",baseURL + lastquery + "&page=" + pagenum);
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final StringRequest movieListRequest = new StringRequest(
                Request.Method.GET,
                baseURL + lastquery + "&page=" + pagenum,
                response -> {
                    parseMovie(movieList,response);
                    Log.d("searchedmovie",movieList.toString());
                    movies.clear();
                    movies.addAll(movieList);
                    movieadapter.notifyDataSetChanged();
                    Log.d("updatedmovies",movies.toString());
                    // Handle response
                },
                error -> {
                    // Handle error
                    Log.e("Volley", "Error occurred", error);
                }
        );
        queue.add(movieListRequest);
    }
    protected void searchMovie(String query){
        ArrayList<Movie> movieList = new ArrayList<>();
        Log.d("wdbwjdb",baseURL + "/search?pagesize=10&sort=ranking&order=DESC&title=" +query + "&page=1" );
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final StringRequest movieListRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/search?page=1&pagesize=10&sort=ranking&order=DESC&title=" +query,
                response -> {
                    parseMovie(movieList,response);
                    Log.d("searchedmovie",movieList.toString());
                    lastquery = "/search?pagesize=10&sort=ranking&order=DESC&title=" +query;
                    pagenum = 1;
                    movies.clear();
                    movies.addAll(movieList);
                    movieadapter.notifyDataSetChanged();
                    Log.d("updatedmovies",movies.toString());
                    // Handle response
                },
                error -> {
                    // Handle error
                    Log.e("Volley", "Error occurred", error);
                }
        );
        queue.add(movieListRequest);



    }
    protected ArrayList<Movie> getMovieList() {
        ArrayList<Movie> movieList = new ArrayList<>();
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final StringRequest movieListRequest = new StringRequest(Request.Method.GET,
                //actually use search. we need to get query from a search page
                baseURL + "/api/movies?page=1&pagesize=10&sort=ranking&order=DESC",
                response -> {
                    try {
                        parseMovie(movieList,response);
                        Log.d("movielist",movieList.toString());
                        movieadapter = new MovieListViewAdapter(this, movieList);
                        lastquery = "/api/movies?pagesize=10&sort=ranking&order=DESC";
                        pagenum = 1;
                        ListView listView = findViewById(R.id.list);
                        listView.setAdapter(movieadapter);
                        listView.setOnItemClickListener((parent, view, position, id) -> {
                            Movie movie = movieList.get(position);
                            @SuppressLint("DefaultLocale") String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getName(), movie.getYear());
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            Intent MovieDetailsPage = new Intent(MovieListActivity.this, MovieDetailsActivity.class);
                            //go to movie page
                            MovieDetailsPage.putExtra("movie", movie);
                            startActivity(MovieDetailsPage);
                        });
                        SearchView searchView = findViewById(R.id.searchBar);
                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                // Handle the search query here
                                Log.d("Usersubmit", query);
                                searchMovie(query);
                                return true;
                            }
                            @Override
                            public boolean onQueryTextChange(String query) {
                                Log.d("UserInput", query);

                                return true;
                            }
                        });
                        searchView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (searchView.hasFocus()) {
                                    searchView.clearFocus();
                                } else {
                                    searchView.requestFocus();
                                }
                            }
                        });
                        Button searchButton = findViewById(R.id.searchButton);

                        // Set the listener
                        searchButton.setOnClickListener(v -> {
                            // Get the current query text
                            String queryText = searchView.getQuery().toString();

                            // Set the text in the SearchView to the current query text and submit it
                            searchView.setQuery(queryText, true);
                        });
                        Button nextButton = findViewById(R.id.nextButton);
                        nextButton.setOnClickListener(v -> {
                            getnext();

                        });
                        Button prevButton = findViewById(R.id.prevButton);
                        prevButton.setOnClickListener(v -> {
                            getlast();

                        });


                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                },
                error -> {
                    Log.d("Error","error");
                });
        queue.add(movieListRequest);
        return movieList;
    }

}