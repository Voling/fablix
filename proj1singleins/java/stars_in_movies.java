public class stars_in_movies {
    private String starId;
    private String movieId;
    public stars_in_movies(String movieId, String starId) {
        this.starId = starId;
        this.movieId = movieId;
    }
    public String getStarId() {
        return starId;
    }
    public String getMovieId() {
        return movieId;
    }
}
