package edu.uci.ics.fabflixmobile.data.model;
import java.io.Serializable;
import java.util.ArrayList;
/**
 * Movie class that captures movie information for movies retrieved from MovieListActivity
 */
public class Movie implements Serializable {
    private final String name;
    private final int year;
    private final String director;
    private final ArrayList<String> genres;
    private final ArrayList<String> stars;

    public Movie(String name, int year, String director, ArrayList<String> genres, ArrayList<String> stars) {
        this.name = name;
        this.year = year;
        this.director = director;
        this.genres = genres;
        this.stars = stars;
    }

    public String getName() {
        return name;
    }

    public int getYear() {
        return year;
    }
    public String getDirector() { return director; }
    public ArrayList<String> getGenres() { return genres; }
    public ArrayList<String> getStars() { return stars; }
}