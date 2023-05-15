import java.util.Objects;

public class star {
    private String id;
    private String name;
    private int birthYear;
    //private int numMovies;

    public star(String id, String name, int birthYear) {
        this.id = id;
        this.name = name;
        this.birthYear = birthYear;
        //this.numMovies = numMovies;
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public int getBirthYear() {
        return birthYear;
    }
    /* 
    public int getNumMovies() {
        return numMovies;
    }
    */
    /* 
    public void incrementNumMovies() {
        numMovies++;
    }
    */
    public int hashCode() {
        return Objects.hash(name, birthYear);
    } //because random id assignment, maybe don't hash based on that
}
