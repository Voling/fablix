public class genres_in_movies {
    private int genreId;
    private String movieId;
    public genres_in_movies(int genreId, String movieId) {
        this.genreId = genreId;
        this.movieId = movieId;
    }
    public int getGenreId() {
        return genreId;
    }
    public String getMovieId() {
        return movieId;
    }
}
