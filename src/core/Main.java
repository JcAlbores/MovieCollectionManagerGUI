
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
            System.out.println("2) View Media");
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
            System.out.println("\n--- View Media ---");
            System.out.println("1) List all entries");
            System.out.println("2) Search media");
            System.out.println("3) Filter by category");
            System.out.println("4) Sort media");
            System.out.println("0) Back");

            int choice = promptInt("Choose an option");
            switch (choice) {
                case 1 -> listAll();
                case 2 -> searchMenu();
                case 3 -> filterByCategory();
                case 4 -> sortMenu();
                case 0 -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }
    
    /**
     * Displays a search menu that allows the user to
     * search media records by ID or by title.
     * The menu loops until the user chooses to go back.
     */
    private void searchMenu() {

        boolean back = false; // controls loop exit

        // Loop until user selects "Back"
        while (!back) {
            System.out.println("\n--- Search Media ---");
            System.out.println("1) Search by ID");
            System.out.println("2) Search by Title");
            System.out.println("0) Back");

            // Get user choice
            int choice = promptInt("Choose an option");

            // Execute corresponding search operation
            switch (choice) {
                case 1 -> searchByIdBinary();     // search using unique ID
                case 2 -> searchByTitle();  // search using title keyword
                case 0 -> back = true;      // exit search menu
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
    private void searchByIdBinary() {

        int id = promptInt("Enter media ID");

        Collection result = collection.binarySearchById(id);

        if (result == null) {
            System.out.println("No record found.");
        } else {
            result.addView();
            System.out.println(describe(result));
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
    
    /**
     * Displays a menu that allows the user to filter media
     * records by category (Movie, Show, or Documentary).
     *
     * This method runs in a loop until the user chooses
     * to return to the previous menu.
     */
    private void filterByCategory() {

        // Controls whether the user exits the filter menu
        boolean back = false;

        // Loop until user selects "Back"
        while (!back) {

            // Display filter options
            System.out.println("\n--- Filter by Category ---");
            System.out.println("1) Movie");
            System.out.println("2) Show");
            System.out.println("3) Documentary");
            System.out.println("0) Back");

            // Read user selection
            int choice = promptInt("Choose an option");

            // Will hold the selected category name
            String category;

            // Map user choice to category
            switch (choice) {
                case 1 -> category = "Movie";
                case 2 -> category = "Show";
                case 3 -> category = "Documentary";

                // Exit filter menu
                case 0 -> {
                    back = true;
                    continue;
                }

                // Handle invalid menu selection
                default -> {
                    System.out.println("Invalid option.");
                    continue;
                }
            }

            // Use shared CollectionManager filtering logic
            // This ensures consistency between CLI and GUI
            List<Collection> results =
                    collection.filterByCategory(collection.getAll(), category);

            // Display results or notify if none found
            if (results.isEmpty()) {
                System.out.println("No records found.");
            } else {
                results.forEach(c -> System.out.println(describe(c)));
            }
        }
    }

    
    /**
     * Displays the sort menu for the Text-Based Interface (TBI)
     * and allows the user to sort media records using shared
     * backend sorting logic.
     *
     * This method reuses the same sorting implementation
     * used by the GUI to ensure consistency across interfaces.
     */
    private void sortMenu() {

        // Controls whether the user wants to return to the previous menu
        boolean back = false;

        // Loop until the user selects the "Back" option
        while (!back) {

            // Display available sort options
            System.out.println("\n--- Sort Media ---");
            System.out.println("1) Title (A–Z)");
            System.out.println("2) Title (Z–A)");
            System.out.println("3) Year (Ascending)");
            System.out.println("4) Year (Descending)");
            System.out.println("5) Rating (High → Low)");
            System.out.println("6) Rating (Low → High)");
            System.out.println("7) Views (High → Low)");
            System.out.println("8) Views (Low → High)");
            System.out.println("9) Category (A–Z)");
            System.out.println("0) Back");

            // Prompt user for sort selection
            int sortKey = promptInt("Choose a sort option");

            // Exit sort menu if user selects "Back"
            if (sortKey == 0) {
                back = true;
                continue;
            }

            // CORE REUSE
            // Delegate sorting to CollectionManager to ensure
            // the same logic is used by both GUI and TBI
            List<Collection> sorted =
                    collection.getSorted(sortKey, collection.getAll());

            // Handle empty collection case
            if (sorted.isEmpty()) {
                System.out.println("No records to sort.");
            } 
            // Display sorted results
            else {
                sorted.forEach(c -> System.out.println(describe(c)));
            }
        }
    }

    
    
    
    // ==============================
    // -- Update / delete media sub-menu 
    // ==============================
    
    // -- Menu for updating records
    private void updateMenu() {
        boolean back = false;

        while (!back) {
            System.out.println("\n--- Update Media ---");
            System.out.println("1) Update Media");
            System.out.println("0) Back");

            int choice = promptInt("Choose an option");

            switch (choice) {
                case 1 -> updateMedia();
                case 0 -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    
    /**
     * Handles the update workflow for a selected media item in the CLI.
     * Allows users to update the rating and type-specific fields
     * based on the actual media type.
     */
    private void updateMedia() {

        // Prompt user to enter the media ID to update
        int id = promptInt("Enter media ID");

        try {
            // Retrieve the media item from the collection using the ID
            Collection c = collection.binarySearchById(id);

            // Controls whether the update menu loop should exit
            boolean back = false;

            // Loop until the user chooses to go back
            while (!back) {

                // Display update menu header with media details
                System.out.println("\n--- Update Options for: "
                        + c.getTitle() + " (" + c.getCategory() + ") ---");

                // Common update option for all media types
                System.out.println("1) Update Rating");

                // Display type-specific update options
                if (c instanceof Movie) {
                    System.out.println("2) Update Movie Type");

                } else if (c instanceof Show) {
                    System.out.println("2) Update Seasons");
                    System.out.println("3) Update Episodes");

                } else if (c instanceof Documentary) {
                    System.out.println("2) Update Subject / Topic");
                }

                // Option to return to the previous menu
                System.out.println("0) Back");

                // Read user menu selection
                int choice = promptInt("Choose an option");

                // Handle user choice
                switch (choice) {

                    // Update rating only (shared across all media types)
                    case 1 -> updateRatingOnly(c);

                    // Update type-specific field using a helper method
                    case 2 -> updateMediaSpecificField(c);

                    // Update episodes (only valid for Show objects)
                    case 3 -> {
                        if (c instanceof Show s) {
                            int episodes = promptInt("Total episodes");
                            s.setEpisodes(episodes);
                            System.out.println("Episodes updated.");
                        } else {
                            System.out.println("Invalid option.");
                        }
                    }

                    // Exit update menu loop
                    case 0 -> back = true;

                    // Handle invalid menu input
                    default -> System.out.println("Invalid option.");
                }
            }

        } catch (IllegalArgumentException e) {
            // Handle case where media ID does not exist
            System.out.println("Entry not found.");
        }
    }
    
    
    /**
     * Updates only the rating of a media item.
     * This operation is common across all media types.
     *
     * parameter c = The Collection object whose rating will be updated
     */
    private void updateRatingOnly(Collection c) {

        // Prompt user for a new rating value (with validation handled elsewhere)
        double rating = promptRating();

        // Update the rating field of the selected media item
        c.setRating(rating);

        // Provide user feedback
        System.out.println("Rating updated.");
    }

    /**
     * Updates media-type-specific fields using polymorphism.
     * The actual field updated depends on the runtime type of the object.
     *
     * parameter c = The Collection object to be updated
     */
    private void updateMediaSpecificField(Collection c) {

        // If the media is a Movie, update its movie type
        if (c instanceof Movie m) {

            // Prompt user to select a movie type
            String movieType = chooseMovieType();

            // Update movie-specific attribute
            m.setMovieType(movieType);

            System.out.println("Movie type updated.");
        }

        // If the media is a Show, update the number of seasons
        else if (c instanceof Show s) {

            // Prompt user for the number of seasons
            int seasons = promptInt("Number of seasons");

            // Update show-specific attribute
            s.setSeasons(seasons);

            System.out.println("Seasons updated.");
        }

        // If the media is a Documentary, update its subject/topic
        else if (c instanceof Documentary d) {

            // Prompt user to enter the documentary subject or topic
            String subject = promptText("Subject / Topic");

            // Update documentary-specific attribute
            d.setSubject(subject);

            System.out.println("Subject updated.");
        }

        // Fallback safety case (should not normally occur)
        else {
            System.out.println("Invalid media type.");
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

