/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core;

// -- Extends the Collection class
public class Show extends Collection {
	// -- "Seasons" stores how many seasons the show has
    private final int seasons;
    // -- "Episodes" stores how many episodes the show has
    private final int episodes;

    // -- Constructor used to create a new show object with all the details
    // -- It takes both the shared attributes (from Collection) and new ones specific to shows
    public Show(int id, String title, String genre, int year, double rating, int seasons, int episodes) {
    	// -- Calls the parent class (Collection) constructor
    	// -- Sets up common information like id, title, genre, year, and rating
        super(id, title, genre, year, rating);
        // -- Assigns the show-specific values for seasons and episodes
        this.seasons = seasons;
        this.episodes = episodes;
    }
    // -- Getter method: returns how many seasons the show has
    public int getSeasons() { 
        return seasons; 
    }
    // --  Getter method: returns how many episodes the show has
    public int getEpisodes() { 
        return episodes; 
    }
    // -- Overrides the getCategory() method from the parent class
    // -- This ensures that when called on a Show object, it returns "Show"
    // helping the program identify this as a TV show or series type
    @Override
    public String getCategory() {
        return "Show";
    }
}

