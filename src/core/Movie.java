/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core;

// -- extends the collection class 

public class Movie extends Collection {
	// -- Declared as final so once set in the constructor, it cannot be changed
    private final String movieType;
    // -- Constructor used to create a new movie object with all its details 
    public Movie(int id, String title, String genre, int year, double rating, String movieType) {
    	// -- Calls the parent class (Collection) constructor
    	// -- Passes the shared attribute (id, title, genre, year, rating) to the parent class handle
        super(id, title, genre, year, rating);
        this.movieType = movieType;
    }
    // -- Getter method: returns the type of movie
    public String getMovieType() {
        return movieType;
    }
    // -- Override the getCategory() method from the parent class
    // -- Ensures that when called on a movie object, it always returns "Movie" 
    @Override
    public String getCategory() {
        return "Movie";
    }
}
