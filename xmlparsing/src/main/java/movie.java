import java.util.Objects;
import java.util.Random;
public class movie {
    private String id;
    private String title;
    private int year;
    private String director;
    float price;
    public movie(String id, String title, int year, String director) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
        price = (float)(Math.random()*10)+1;
    }
    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public int getYear() {
        return year;
    }
    public String getDirector() {
        return director;
    }
    public float getPrice() {
        return price;
    }
    public void setDirector(String dirName) {
        director = dirName;
    }
    public int hashCode() {
        return Objects.hash(title, year, director);
    } //because random id assignment, cannot hash based on that
}
