/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

// -- The Main class is the entry point of the Movie Collection Manager program
// -- It handles the main menu, user inputs, and calls methods from CollectionManager
//to manage movies, shows, and documentaries
public class Main {
	
	// -- The CSV file where data is saved and loaded
    private static final String DATA_FILE = "MovieCollection.csv";
    
    // -- Used to read user input from the console    
    private final Scanner scanner;
    // -- Handles all operations related to managing the collection data
    
    private final CollectionManager collection;
    // -- Constructor: initializes the Scanner and CollectionManager
    public Main() {
        this.scanner = new Scanner(System.in);
        this.collection = new CollectionManager();
    }
    // -- The main method starts the application
    public static void main(String[] args) {
        new Main().startCLI();
    }
    
    public void startCLI() {
    	// Load existing movie collection data from csv
        loadData();
        
        // Flag to control the main program loop
        boolean running = true;

        // Main loop that keeps the CLI running until the user chooses to exit
        while (running) {
            System.out.println("\n===== MOVIE COLLECTION MANAGER =====");
            System.out.println("1) Media Management");
            System.out.println("2) Evaluation & Rankings");
            System.out.println("3) File Operations");
            System.out.println("0) Save and Exit");

            // Prompt the user to enter a menu option and store the choice
            int choice = promptInt("Choose an option");

            // Handle the user's menu selection using a switch expression
            switch (choice) {
                case 1 -> mediaMenu();
                case 2 -> evaluationMenu();
                case 3 -> fileMenu();
                case 0 -> {
                    saveData();  // Persist current data before exiting
                    running = false; // Exit the main loop
                }
                
                // Handle invalid menu selections
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
        
        // Close the scanner to release system resources
        scanner.close();
        System.out.println("Sad to see you go :( See you soon!");
    }

    
    // ==============================
    // -- Media management
    // ==============================
    
    // -- Handles the media management menu
    // -- Allows adding, viewing, updating, or deleting media entries
    private void mediaMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Media Management ---");
            System.out.println("1) Add Media");
            System.out.println("2) View / Search Media");
            System.out.println("3) Update Media");
            System.out.println("4) Delete Media");
            System.out.println("0) Back");

            int choice = promptInt("Choose an option");
            switch (choice) {
                case 1 -> addMenu();
                case 2 -> viewMenu();
                case 3 -> updateMenu();
                case 4 -> deleteMedia();
                case 0 -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }
    
    // ==============================
    // -- Add media sub-menu 
    // ==============================
    
    // --  Menu for choosing what kind of media to add
    private void addMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Add Media ---");
            System.out.println("1) Add Movie");
            System.out.println("2) Add Show");
            System.out.println("3) Add Documentary");
            System.out.println("0) Back");

            int choice = promptInt("Choose an option");
            switch (choice) {
                case 1 -> addMovie();
                case 2 -> addShow();
                case 3 -> addDocumentary();
                case 0 -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }
    // -- Adds a new movie by collecting details from the user
    private void addMovie() {
        String title = promptText("Title");
        String genre = promptText("Genre");
        int year = promptYear();
        double rating = promptRating();
        String movieType = chooseMovieType();

        collection.addMovie(title, genre, year, rating, movieType);
        System.out.println("Movie added successfully.");
    }
    // -- Adds a new show with seasons and episodes information
    private void addShow() {
        String title = promptText("Title");
        String genre = promptText("Genre");
        int year = promptYear();
        double rating = promptRating();
        int seasons = promptInt("Number of seasons");
        int episodes = promptInt("Total episodes");

        collection.addShow(title, genre, year, rating, seasons, episodes);
        System.out.println("Show added successfully.");
    }
    // -- Adds a new documentary with a specific subject
    private void addDocumentary() {
        String title = promptText("Title");
        String genre = promptText("Genre");
        int year = promptYear();
        double rating = promptRating();
        String subject = promptText("Subject / Topic");

        collection.addDocumentary(title, genre, year, rating, subject);
        System.out.println("Documentary added successfully.");
    }
	// ==============================
    // -- View media sub-menu 
    // ==============================
    
    // -- Menu for viewing or searching media in the collection
    private void viewMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- View / Search Media ---");
            System.out.println("1) List all entries");
            System.out.println("2) View by ID");
            System.out.println("3) Search by title");
            System.out.println("0) Back");

            int choice = promptInt("Choose an option");
            switch (choice) {
                case 1 -> listAll();
                case 2 -> viewById();
                case 3 -> searchByTitle();
                case 0 -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }
    
    // -- Displays all media in the collection
    private void listAll() {
        List<Collection> items = collection.getAll();
        if (items.isEmpty()) {
            System.out.println("No entries found.");
            return;
        }
        for (Collection c : items) {
            System.out.println(describe(c));
        }
    }
    
    // -- Displays details for a media item based on its ID
    private void viewById() {
        int id = promptInt("Enter ID");
        try {
            Collection c = collection.getById(id);
            c.addView(); // counts as a view
            System.out.println(describe(c));
        } catch (IllegalArgumentException e) {
            System.out.println("Entry not found.");
        }
    }
    
    // -- Lets the user search for media by title or partial match
    private void searchByTitle() {
        String keyword = promptText("Enter full or partial title");
        List<Collection> results = collection.findByTitle(keyword);
        if (results.isEmpty()) {
            System.out.println("No matches found.");
            return;
        }
        for (Collection c : results) {
            System.out.println(describe(c));
        }
    }
    
    // ==============================
    // -- Update / delete media sub-menu 
    // ==============================
    
    // -- Menu for updating rating or view counts
    private void updateMenu() {
        boolean back = false;

        while (!back) {
            System.out.println("\n--- Update Media ---");
            System.out.println("1) Update Rating");
            System.out.println("2) Add Views");
            System.out.println("0) Back");

            int choice = promptInt("Choose an option");

            switch (choice) {
                case 1 -> updateRating();
                case 2 -> updateViews();
                case 0 -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }
    
    // -- Updates the rating of a media item by ID
    private void updateRating() {
        int id = promptInt("Enter media ID");

        try {
            double rating = promptRating();
            collection.updateRating(id, rating);
            System.out.println("Rating updated successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Entry not found.");
        }
    }

    // -- Adds extra views to a media item
    private void updateViews() {
        int id = promptInt("Enter media ID");

        try {
            int viewsToAdd = promptInt("Number of views to add");
            collection.addViews(id, viewsToAdd);
            System.out.println("Views updated successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Entry not found.");
        }
    }

    // -- Deletes a media item based on ID
    private void deleteMedia() {
        int id = promptInt("Enter ID to delete");
        if (collection.delete(id)) {
            System.out.println("Entry deleted.");
        } else {
            System.out.println("Entry not found.");
        }
    }
    
    // ==============================
    // -- Evaluation menu 
    // ==============================
    
    // -- Menu for viewing top-ranked or most-viewed items
    private void evaluationMenu() {
        boolean back = false;

        while (!back) {
            System.out.println("\n--- Evaluation & Rankings ---");
            System.out.println("1) View Top Ranked Movies");
            System.out.println("2) View Most Viewed Shows");
            System.out.println("3) View Highly Rated Documentaries");
            System.out.println("0) Back");

            int choice = promptInt("Choose an option");

            switch (choice) {
                case 1 -> viewTopMoviesByYear();
                case 2 -> viewMostViewedShows();
                case 3 -> viewHighlyRatedDocumentaries();
                case 0 -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }
    
    // -- Displays top-ranked movies for a specific year
    private void viewTopMoviesByYear() {

        int year = promptYear();
        System.out.println("\n=== Top Ranked Movies (" + year + ") ===");

        List<Movie> topMovies = collection.getTopMoviesByYear(year);

        if (topMovies.isEmpty()) {
            System.out.println("No movies available for " + year + ".");
            return;
        }

        int rank = 1;
        for (Movie m : topMovies) {
            System.out.println("Top " + rank++ + ": " + describe(m));
        }
    }

    // -- Displays most-viewed TV shows
    private void viewMostViewedShows() {
        System.out.println("\n=== Most Viewed Shows ===");

        List<Show> topShows = collection.evaluateTopShowsByViews();

        if (topShows.isEmpty()) {
            System.out.println("No shows available.");
            return;
        }

        int rank = 1;
        for (Show s : topShows) {
            System.out.println("Top " + rank++ + ": " + describe(s));
        }
    }
    
    // -- Displays highly rated documentaries
    private void viewHighlyRatedDocumentaries() {
        System.out.println("\n=== Highly Rated Documentaries ===");

        List<Documentary> topDocs = collection.evaluateTopDocumentariesByRating();

        if (topDocs.isEmpty()) {
            System.out.println("No documentaries available.");
            return;
        }

        int rank = 1;
        for (Documentary d : topDocs) {
            System.out.println("Top " + rank++ + ": " + describe(d));
        }
    }

    // ==============================
    // -- File operations menu 
    // ==============================
    
    // -- Menu for loading and saving data files
    private void fileMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- File Operations ---");
            System.out.println("1) Load data from file");
            System.out.println("2) Save data to file");
            System.out.println("0) Back");

            int choice = promptInt("Choose an option");
            switch (choice) {
                case 1 -> loadData();
                case 2 -> saveData();
                case 0 -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }
    
    // ==============================
    // -- File handling methods
    // ==============================
    
    // -- Loads stored collection data from a CSV file
    private void loadData() {
        try {
            collection.loadFromFile(DATA_FILE);
            System.out.println("Data loaded from " + DATA_FILE + ".");
        } catch (FileNotFoundException e) {
            System.out.println("No data file found. Starting fresh.");
        } catch (IOException e) {
            System.out.println("Failed to load data: " + e.getMessage());
        }
    }

    // -- Saves current collection data into the CSV file
    private void saveData() {
        System.out.print("This will overwrite the csv data. Continue? Y/N ");
        String yes = scanner.nextLine().trim();
        if (!yes.equalsIgnoreCase("y")) {
            return;
        }

        try {
            collection.saveToFile(DATA_FILE);
            System.out.println("Data saved to " + DATA_FILE + ".");
        } catch (IOException e) {
            System.out.println("Failed to save data: " + e.getMessage());
        }
    }
    
    // ==============================
    // -- Input helpers 
    // ==============================
    
    // -- Prompts user for text input and ensures it's not empty
    private String promptText(String label) {
        while (true) {
            System.out.print(label + ": ");
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("This field cannot be empty.");
        }
    }

    // -- Prompts user for an integer input (e.g., year, ID)
    private int promptInt(String label) {
        while (true) {
            System.out.print(label + ": ");
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    // -- Prompts user for a valid year between 1900 and 2100
    private int promptYear() {
        while (true) {
            int year = promptInt("Year (YYYY)");
            if (year >= 1900 && year <= 2100)
                return year;
            System.out.println("Enter a valid year (1900 - 2100).");
        }
    }

    // -- Prompts user for a rating between 0.0 and 10.0
    private double promptRating() {
        while (true) {
            System.out.print("Rating (0.0 - 10.0): ");
            try {
                double r = Double.parseDouble(scanner.nextLine().trim());
                if (r >= 0.0 && r <= 10.0)
                    return r;
                System.out.println("Rating must be between 0.0 and 10.0.");
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid decimal number.");
            }
        }
    }

    // -- Prompts user to choose a valid movie type
    private String chooseMovieType() {
        String[] allowed = {"indie", "blockbuster", "short", "classic"};
        while (true) {
            System.out.print("Movie type (Indie | Blockbuster | Short | Classic): ");
            String t = scanner.nextLine().trim().toLowerCase();
            for (String option : allowed) {
                if (option.equals(t)) {
                    return t;
                }
            }
            System.out.println("Invalid movie type.");
        }
    }

    // ============================== 
    // -- Description helper 
    // ==============================
    
    // -- Builds and returns a formatted text description of a media item
    private String describe(Collection c) {
        String info = "#" + c.getId() + " | " + c.getTitle() +
                " (" + c.getYear() + ") | " + c.getGenre() +
                " | Rating: " + c.getRating() +
                " | Views: " + c.getViews() +
                " | Category: " + c.getCategory();
        
        // -- Adds [TOP 3] tag if the item is top-ranked
        if (c.isTopRanked()) info += " [TOP 3]";

        // -- Adds extra details specific to Shows or Documentaries  
        if (c instanceof Show s) {
            info += " | Seasons: " + s.getSeasons() +
                    " | Episodes: " + s.getEpisodes();
        }
        if (c instanceof Documentary d) {
            info += " | Subject: " + d.getSubject();
        }
        return info;
    }
}

