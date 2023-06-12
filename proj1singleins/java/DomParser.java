import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DomParser {
/*
    List<star> stars = new ArrayList<>();
    List<movie> movies = new ArrayList<>();
    List<genre> genres = new ArrayList<>();
    List<stars_in_movies> casts = new ArrayList<>();
    List<genres_in_movies> genreVariety = new ArrayList<>();
*/
    Logger logger;
    FileHandler fh;
    Logger movieduplog;
    FileHandler moviedupfh;
    Logger starduplog;
    FileHandler stardupfh;
    Logger movienotfoundlog;
    FileHandler mnotfoundfh;
    Logger starnotfoundlog;
    FileHandler starnotfoundfh;
    HashMap<String, Boolean> movielookup = new HashMap<>();
    HashMap<String, Boolean> movieidlookup = new HashMap<>();
    ArrayList<movie> movies = new ArrayList<>();
    HashMap<String, String> starlookup = new HashMap<>();
    ArrayList<star> stars = new ArrayList<>();
    HashMap<String, Integer> genrelookup = new HashMap<>();
    ArrayList<genre> genres = new ArrayList<>();
    //HashMap<stars_in_movies, Boolean> casts = new HashMap<>();
    //HashMap<genres_in_movies, Boolean> GenresInMovies = new HashMap<>();
    ArrayList <stars_in_movies> casts = new ArrayList<>();
    ArrayList <genres_in_movies> GenresInMovies = new ArrayList<>();
    int lastmovieid;
    int laststarid;
    int lastgenreid;
    //ensure duplication prevention
    //but do NOT load existing movies and stars from database. we will deplete ram
    Document dom;

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/moviesqlback";
    private static final String DATABASE_USER = "root";
    //mytestuser
    private static final String DATABASE_PASSWORD = "3t1415926"; 
    Connection connection;
    public DomParser() {
        try {
            connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
            logger = Logger.getLogger("inconsistencyReportLogger");
            fh = new FileHandler("XMLParserLog.txt");
            logger.addHandler(fh);
            //
            movieduplog = Logger.getLogger("movieduplicatelogger");
            moviedupfh = new FileHandler("duplicatemovies.txt");            
            movieduplog.addHandler(moviedupfh);
            //
            starduplog = Logger.getLogger("starduplicatelogger");
            stardupfh = new FileHandler("duplicatestars.txt");
            starduplog.addHandler(stardupfh);
            //
            movienotfoundlog = Logger.getLogger("movienotfoundlogger");
            mnotfoundfh = new FileHandler("movienotfound.txt");
            movienotfoundlog.addHandler(mnotfoundfh);

            starnotfoundlog = Logger.getLogger("starnotfoundlogger");
            starnotfoundfh = new FileHandler("starnotfound.txt");
            starnotfoundlog.addHandler(starnotfoundfh);
            
            
            
            connection.setAutoCommit(false);
        }
        catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        // parse the XML file and get the DOM object
        getmaindata();
        parseXmlFile("mains243.xml");
        parseMainsDocument();

        parseXmlFile("actors63.xml");
        parseActorsDocument();



        parseXmlFile("casts124.xml");
        parseCastsDocument();
        printall();

        // iterate through the list and insert data into the database
        insertData();

    }
    private void printall(){
        System.out.println(movielookup.toString().substring(0,Math.min(movieidlookup.toString().length(), 200)));
        System.out.println(movieidlookup.toString().substring(0, Math.min(movieidlookup.toString().length(), 200)));
        System.out.println(starlookup.toString().substring(0, Math.min(starlookup.toString().length(), 200)));
        System.out.println(genrelookup.toString().substring(0, Math.min(genrelookup.toString().length(), 200)));
        System.out.println(stars.toString().substring(0, Math.min(stars.toString().length(), 200)));
        System.out.println(stars.size());
        System.out.println(movies.toString().substring(0, Math.min(movies.toString().length(), 200)));
        System.out.println(movies.size());
        System.out.println(genres.toString().substring(0, Math.min(genres.toString().length(), 200)));
        System.out.println(genres.size());
        System.out.println(casts.toString().substring(0, Math.min(casts.toString().length(), 200)));
        System.out.println(casts.size());
        System.out.println(GenresInMovies.toString().substring(0, Math.min(GenresInMovies.toString().length(),200)));
        System.out.println(GenresInMovies.size());
        

    }

    
    private void parseXmlFile(String path) {
        // get the factory
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            // using factory get an instance of document builder
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            // parse using builder to get DOM representation of the XML file
            dom = documentBuilder.parse(path);
        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }
    public void getmaindata(){
       

        //Class.forName("com.mysql.jdbc.Driver").newInstance();
            try{
            // Create a new statement
            Statement statement3 = connection.createStatement();

   

    // Perform your operations here

    // After completing the operations, enable foreign key checks again
   

            Statement statement = connection.createStatement();
            String query = "select id, name from stars order by id DESC;";
            // build hashmap
            ResultSet rs = statement.executeQuery(query);
            int counter2 = 0;
            while (rs.next()) {
              
                String name = rs.getString("name");
                String id = rs.getString("id");
                if (counter2 == 0){
                    try{
                    laststarid= Integer.parseInt(id.substring(2));
                    }
                    catch(Exception e){
                        starlookup.put(name, id);
                        continue;
                    }

                }
                starlookup.put(name, id);
                counter2 += 1;
            }
            statement.close();
            Statement statement1 = connection.createStatement();
            String query1 = "select title ,id from movies order by id DESC;";
            // build hashmap
            int counter1 = 0;
            ResultSet rs1 = statement1.executeQuery(query1);
            while (rs1.next()) {
               
                String name = rs1.getString("title");
                String id = rs1.getString("id");
                //System.out.println("ID from DB: " + id + ", Name: " + name);
                if(counter1 == 0){
                    try{
                    lastmovieid= Integer.parseInt(id.substring(2));
                    }
                    catch(Exception e){
                        movieidlookup.put(id,true);
                        movielookup.put(name, true);
                        continue;
                    }

                }
                movieidlookup.put(id,true);
                movielookup.put(name, true);
                counter1 += 1;
            }
            System.out.println(movieidlookup.get("ZlK20"));
            System.out.println("Movie ID Lookup: " + movieidlookup);
            statement1.close();

            Statement statement2 = connection.createStatement();
            String query2 = "select name, id from genres order by id DESC;";
            // build hashmap
            ResultSet rs2 = statement2.executeQuery(query2);
            int counter = 0;
            while (rs2.next()) {
              
                String name = rs2.getString("name");
                int id = rs2.getInt("id");
                if (counter == 0){
                    lastgenreid = id;
                }
                genrelookup.put(name, id);
                counter += 1;
            }
            statement2.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }


       
    }
    private void parseMainsDocument() {
        /*
            movies
            -    directorfilms
            -        director
            -            (dirname)
            -        films
            -            t (title)
            -            year (year)
         */
        // get the document root element
        Element documentElement = dom.getDocumentElement();
        //System.out.println(documentElement.toString());

    // get a NodeList of directorfilms elements
        NodeList parent = documentElement.getElementsByTagName("directorfilms");
        //System.out.println(parent.toString());
        
        for (int i = 0; i < parent.getLength(); i++) {
            String director = null;
            Element directorFilmsList = (Element) parent.item(i);
            NodeList directorDetails = directorFilmsList.getElementsByTagName("director");
            for (int x = 0; x < directorDetails.getLength(); x++) {
                Element directorElement = (Element) directorDetails.item(x);
                director = parseDirector(directorElement);
                        //should only be 1 director
            } //but iterate anyway
            NodeList allDirectorFilmsDetails = directorFilmsList.getElementsByTagName("film");
            for (int j = 0; j < allDirectorFilmsDetails.getLength(); j++) {
                Element film = (Element) allDirectorFilmsDetails.item(j);
                //System.out.println(film.toString());
                if(getTextValue(film, "fid") == null || getTextValue(film, "t") == null){
                    //logger.log(Level.WARNING, "Inconsistent movie data: " + getTextValue(film, "t") + " - ", getTextValue(film, "fid"));
                    continue;
                }
                else{
                if(movielookup.containsKey(getTextValue(film, "t")) || movieidlookup.containsKey(getTextValue(film, "fid"))){
                    //write it to log
                   movieduplog.log(Level.WARNING, "duplicate: " + getTextValue(film, "t") + getTextValue(film,"fid"));
                    continue;
        
                }
                else{
                movie movie = parseMovie(film);
                movie.setDirector(director);
                //find film details
                //now find categories (genres)
                //skip if year is -1 (error condition)
            
                   
                    NodeList allGenres = film.getElementsByTagName("cats");
                    for (int z = 0; z < allGenres.getLength(); z++) {
                        Element cat = (Element) allGenres.item(z);
                        if(!genrelookup.containsKey(cat.getTextContent())){
                        int newid = lastgenreid + 1;
                        lastgenreid = newid;
                        genre filmGenre = new genre(newid, cat.getTextContent());
                        //ArrayList<genre> allGenresOfMovie = new ArrayList<>();
                        genres.add(filmGenre);
                        genrelookup.put(cat.getTextContent(), filmGenre.getId());
                        genres_in_movies genreFilmAssociation = new genres_in_movies(filmGenre.getId(),movie.getId());
                        GenresInMovies.add(genreFilmAssociation);
                        }
                        else{
                        genres_in_movies genreFilmAssociation = new genres_in_movies(genrelookup.get(cat.getTextContent()), movie.getId());
                        GenresInMovies.add(genreFilmAssociation);

                        }
                    }
                movies.add(movie);
                movielookup.put(movie.getTitle(), true);
                movieidlookup.put(movie.getId(), true);
                if(movie.getId() == null){
                    System.out.println("fucking null");
                }
               
               
            }
            }
        }


            } //observe hierarchy. also has genres
            //also need to make genres
            // add it to the list

        }
    

    private void parseActorsDocument() { //stars
        Element documentElement = dom.getDocumentElement();
       
            NodeList allActors = documentElement.getElementsByTagName("actor");
            //go into actor
            for (int j = 0; j < allActors.getLength(); j++) {
                Element actorElement = (Element) allActors.item(j);
                if(!starlookup.containsKey(getTextValue(actorElement, "stagename"))){
                star star1 = parseStar(actorElement);
                stars.add(star1);
                starlookup.put(star1.getName(),star1.getId());
                }
                else{
                    //report to duplicate
                    starduplog.log(Level.WARNING, "duplicate: " + getTextValue(actorElement, "stagename"));
        


                }
            }
        
    }

    private void parseCastsDocument() {
        Element documentElement = dom.getDocumentElement();
        System.out.println(documentElement.toString());
    
      
            NodeList filmList = documentElement.getElementsByTagName("dirfilms");
            for (int j = 0; j < filmList.getLength(); j++) {
                Element filmElement = (Element) filmList.item(j);
                // for each cast (member entry),
                NodeList castList = filmElement.getElementsByTagName("filmc");
                for (int k = 0; k < castList.getLength(); k++) {
                    Element castElement = (Element) castList.item(k);
                    NodeList associationList = castElement.getElementsByTagName("m");
                    for (int z = 0; z < associationList.getLength(); z++) {
                        Element starMovieAssociation = (Element) associationList.item(z);
                        if(movieidlookup.containsKey(getTextValue(starMovieAssociation, "f"))){
                            if(starlookup.containsKey(getTextValue(starMovieAssociation, "a"))){
                                stars_in_movies star_in_movie = parseCast(starMovieAssociation);
                                casts.add(star_in_movie);
                            }
                            else{
                                //report missing star
                                starnotfoundlog.log(Level.SEVERE,"not found" + getTextValue(starMovieAssociation, "a"));
        
                               

                            }
                        }
                        else{
                            //report missing movie 
                            movienotfoundlog.log(Level.SEVERE,"not found " + getTextValue(starMovieAssociation, "f"));
        
                        }
                        
                    }
                }
            
        }
    }


    private stars_in_movies parseCast(Element element) {
        String movieId = getTextValue(element, "f");
        String starId = starlookup.get(getTextValue(element, "a"));
        return new stars_in_movies(movieId, starId);
    }

    private star parseStar(Element element) {
        // for each <actor> element get values of name, birthYear, and num_movies
        //give it an id
        try {
            int idnum = laststarid+1;
            laststarid = idnum;
            String id = String.valueOf(idnum);
             // don't generate random string
            String name = getTextValue(element, "stagename");
            int birthYear = getIntValue(element, "dob");
            //int numMovies = 0; //discovered later

            return new star(id, name, birthYear);
        }
        catch (NumberFormatException e) {
            int idnum = laststarid+1;
            laststarid = idnum;
            String id = String.valueOf(idnum);
            logger.log(Level.WARNING, "Inconsistent STAR data: " + getTextValue(element, "stagename") + " - ", getTextValue(element, "dob"));
            return new star(id, getTextValue(element, "stagename"), -1); //-1 is error condition
        }

    }

    private movie parseMovie(Element element) {
        try {
            String id = getTextValue(element, "fid");
            if(id == null){
                System.out.println("fucking null1");
            }
            //String director = getTextValue(element, "dirname");
            String title = getTextValue(element, "t");
            int year = getIntValue(element, "released");
            return new movie(id, title, year, ""); //director determined later
        }
        catch (NumberFormatException e) {
            
            //String director = getTextValue(element, "dirname");
            String title = getTextValue(element, "t");
            String id = getTextValue(element, "fid");
            if(id == null){
                System.out.println("fucking null2");
            }
           
            return new movie(id,title, -1, "");
        }
    } //if string inserts into int
    private String parseDirector(Element element) {
        return getTextValue(element, "dirname");
    } //for mains
    private genre parseGenre(Element element) {
        int id = utils.generateRandomNumber(10000, 99999); //doesnt matter, database makes it AUTOINCREMENT
        String name = getTextValue(element, "cat"); //
        return new genre(id, name);
    } //do not use

    /**
     * It takes an XML element and the tag name, looks for the tag, and gets the text content
     * For example, for <actor><stagename>John</stagename></actor> XML snippet,
     * if the Element points to actor node and tagName is stagename, it will return John
     */
    private String getTextValue(Element element, String tagName) {
        String textVal = null;
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {

            textVal = nodeList.item(0).getTextContent();
        }
        return textVal;
    }

    private int getIntValue(Element ele, String tagName) {
        // in production application you would catch the exception
        try {
            return Integer.parseInt(getTextValue(ele, tagName));
        }
        catch (NumberFormatException e) {
            //we must throw again to skip
            throw new NumberFormatException("Tried to get int but got string instead: " + getTextValue(ele, tagName));
        }

    }

    private void insertData() {
        int batchSize = 500; //strings might become too big and consume too much ram
        int count = 0;
        String starQuery = "INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?)";
        String movieQuery = "INSERT INTO movies (id, title, year, director, price) VALUES (?, ?, ?, ?, ?)";
        String genreQuery = "INSERT INTO genres (name) VALUES (?)";
        String starMovieQuery = "INSERT INTO stars_in_movies (starId, movieId) VALUES (?, ?)";
        String genreMovieQuery = "INSERT INTO genres_in_movies (genreId, movieId) VALUES (?, ?)";

        try {
            connection.setAutoCommit(false); //allow rollbacks in case of insertion error
            PreparedStatement starStatement = connection.prepareStatement(starQuery);
            PreparedStatement movieStatement = connection.prepareStatement(movieQuery);
            PreparedStatement genreStatement = connection.prepareStatement(genreQuery);
            PreparedStatement starMovieStatement = connection.prepareStatement(starMovieQuery);
            PreparedStatement genreMovieStatement = connection.prepareStatement(genreMovieQuery);
            int starlen = stars.size();
            int movielen = movies.size();
            int genrelen = genres.size();
            int castlen = casts.size();
            int ginmovies = GenresInMovies.size();
            int starcount = 0;
            int moviecount = 0;
            int genrecount = 0;
            int castcount = 0;
            int ginmoviescount = 0;
            int totalprocessed = 0;

            for (star star : stars) {
                // batch insert star data into the "stars" table in the database
                starStatement.setString(1, star.getId());
                starStatement.setString(2, star.getName());
                starStatement.setInt(3, star.getBirthYear());
                starcount += 1;
                totalprocessed += 1;
                starStatement.addBatch();
                if(starcount >= batchSize || totalprocessed == starlen ){
                    starStatement.executeBatch();
                    connection.commit();
                    starStatement.clearBatch();
                    starcount = 0;
                }
                //need to calculate num_movies now that xml data is complete
                
               
                //count = getCount(batchSize, count, starStatement);
            }
        System.out.println("star processed");
        System.out.println(totalprocessed);
        totalprocessed = 0;
           // count = getLeftoverCount(count, starStatement);

            for (movie movie : movies) {
                if (movie.getId() == null || movie.getDirector() == null) {
                    System.out.println("Null ID /director detected in movie: " + movie.getTitle());
                    continue;
                }
                movieStatement.setString(1, movie.getId()); //random id assignment
                movieStatement.setString(2, movie.getTitle());
                movieStatement.setInt(3, movie.getYear());
                movieStatement.setString(4, movie.getDirector());
                movieStatement.setFloat(5, movie.getPrice());
                moviecount += 1;
                totalprocessed += 1;

                movieStatement.addBatch();
                if(moviecount >= batchSize || totalprocessed == movielen ){
                    movieStatement.executeBatch();
                    connection.commit();
                    movieStatement.clearBatch();
                    moviecount = 0;
                }
                //count = getCount(batchSize, count, movieStatement);
            }
            System.out.println("movie processed");
            System.out.println(totalprocessed);
            totalprocessed = 0;

            //count = getLeftoverCount(count, movieStatement);
            
            for (genre genre: genres) {
                genreStatement.setString(1, genre.getName());
                genreStatement.addBatch();
                totalprocessed += 1;
                //count = getCount(batchSize, count, genreStatement);
                //do not need to insert id due to AUTOINCREMENT
            }
            genreStatement.executeBatch();
            connection.commit();
            genreStatement.clearBatch();
            System.out.println("genre processed");
            System.out.println(totalprocessed);
            totalprocessed = 0;

            //count = getLeftoverCount(count, genreStatement);


            for (stars_in_movies star_in_movie: casts) {
                starMovieStatement.setString(1, star_in_movie.getStarId());
                starMovieStatement.setString(2, star_in_movie.getMovieId());
                castcount += 1;
                totalprocessed += 1;
                starMovieStatement.addBatch();
                if(castcount >= batchSize || totalprocessed == castlen ){
                    starMovieStatement.executeBatch();
                    connection.commit();
                    starMovieStatement.clearBatch();
                    castcount = 0;
                }
                //count = getCount(batchSize, count, starMovieStatement);
            }
            System.out.println("cast processed");
            System.out.println(totalprocessed);
            totalprocessed = 0;
            //count = getLeftoverCount(count, starMovieStatement);

            for (genres_in_movies genre_in_movie: GenresInMovies) {
                genreMovieStatement.setInt(1, genre_in_movie.getGenreId());
                genreMovieStatement.setString(2, genre_in_movie.getMovieId());
                ginmoviescount += 1;
                totalprocessed += 1;
                genreMovieStatement.addBatch();
                if(ginmoviescount >= batchSize || totalprocessed == ginmovies ){
                    starMovieStatement.executeBatch();
                    connection.commit();
                    starMovieStatement.clearBatch();
                    ginmoviescount = 0;
                }
                //count = getCount(batchSize, count, genreMovieStatement);
            }
            //count = getLeftoverCount(count, genreMovieStatement);
            starStatement.close();
            movieStatement.close();
            genreStatement.close();
            starMovieStatement.close();
            genreMovieStatement.close();
           // connection.setAutoCommit(true); 
            connection.close();

            System.out.println("Data inserted successfully into the database.");


        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private int getLeftoverCount(int count, PreparedStatement preparedStatement) throws SQLException {
        if (count != 0) {
            try {
                preparedStatement.executeBatch();
                connection.commit();
                preparedStatement.clearBatch();
            }
            catch (SQLException e) {
                logger.log(Level.WARNING, "Insertion went wrong: " + e.getMessage());
                connection.rollback(); //data violated constraint? rollback and skip
            }
        } //get rid of leftovers
        count = 0;
        return count;
    }

    private int getCount(int batchSize, int count, PreparedStatement preparedStatement) throws SQLException {
        if (count == batchSize) {
            try {
                count = 0;
                preparedStatement.executeBatch();
                connection.commit();
                preparedStatement.clearBatch();
            }
            catch (SQLException e) {
                logger.log(Level.WARNING, "Insertion went wrong: " + e.getMessage());
                connection.rollback(); //data violated constraint? rollback and skip
            }
        }
        count++;
        return count;
    }

    public static void main(String[] args) {
        // Create an instance
        DomParser domParser = new DomParser();
        // run
        domParser.run();
    }
}