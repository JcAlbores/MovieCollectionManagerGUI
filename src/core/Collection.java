
package core;

// -- Parent class for movies and shows
public abstract class Collection {
	// -- Unique id for each entry
    private final int id;
    // -- Basic information for every movie or shows  
    private final String title;
    private final String genre;
    private final int year;
    private double rating; // -- The rating can be changed by the user so we did not marked it as final
    private int views;
    private boolean topRanked;

    // -- Constructor to set up the collection's main details
    protected Collection(int id, String title, String genre, int year, double rating) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.rating = rating;
        this.views = 0;
        this.topRanked = false;
    }
    // -- Getters to access the data fields
    public int getId() { 
        return id; 
    }

    public String getTitle() { 
        return title; 
    }

    public String getGenre() { 
        return genre; 
    }

    public int getYear() {
        return year; 
    }

    public double getRating() {
        return rating;
    }

    public int getViews() {
        return views;
    }

    public boolean isTopRanked() { 
        return topRanked; 
    }
    // -- Allows the user to change the rating after creation
    public void setRating(double rating) { 
        this.rating = rating; 
    }
    // -- Increases the number of views by 1 each time its called
    public void addView() { 
        views++; 
    }
    // -- This method sets whether the movie or show is top ranked or not
    public void setTopRanked(boolean value) { 
        this.topRanked = value; 
    }
    // -- Returns the simple class name (like "movie" or "show")
    public String getType() {
        return getClass().getSimpleName();
    }
    // -- Abstract method
    public abstract String getCategory(); // e.g., "Movie" or "Show"
}
