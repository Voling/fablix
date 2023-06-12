import java.util.Objects;

public class genre {
    private String name;
    private int id;

    public genre(int id, String name) {
        this.name = name;
        this.id = id;

    }
    public int getId(){
        return id;
    }
    public String getName() {
        return name;
    }
    public int hashCode() {
        return Objects.hash(name);
    } //because random id assignment, cannot hash based on that
}
