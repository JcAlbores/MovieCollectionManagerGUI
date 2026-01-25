
package core;

/**
 *
 * @author carlo
 */
import java.io.*;
import java.util.*;

// -- The CollectionManager class is responsible for managing all media items (Movies, Shows, Documentaries)
// -- It handles adding, searching, updating, and saving or loading data from a file
public class CollectionManager {

	// -- Stores all media items (Movie, Show, Documentary) in a list
    private final List<Collection> items = new ArrayList<>();
    
    // -- A quick lookup map for finding media by their ID
    private final Map<Integer, Collection> indexById = new HashMap<>();
    
    // -- Keeps track of the next available unique ID for new items
    private int nextId = 1;

    // -- Determines how many top results to show (for rankings)
    private static final int TOP_N = 3;
    
    // Stores sort options
    private final Map<Integer, String> sortOptions = new HashMap<>();
    
    //constructor, initialize sortOptions Hashmap
    public CollectionManager() {

        sortOptions.put(1, "TITLE_ASC");
        sortOptions.put(2, "TITLE_DESC");
        sortOptions.put(3, "YEAR_ASC");
        sortOptions.put(4, "YEAR_DESC");
        sortOptions.put(5, "RATING_DESC");
        sortOptions.put(6, "RATING_ASC");
        sortOptions.put(7, "VIEWS_DESC");
        sortOptions.put(8, "VIEWS_ASC");
        sortOptions.put(9, "CATEGORY_ASC");
    }


    // ==============================
    // -- Basic Operations
    // ==============================

    // -- Returns a new list containing all media items
    // -- (A copy is returned to prevent modification of the original data)
    public List<Collection> getAll() {
        return new ArrayList<>(items);
    }

    // -- Finds and returns a media item using its ID
    // -- Throws an error if it does not exist
    public Collection getById(int id) {
        Collection item = indexById.get(id);
        if (item == null) {
            throw new IllegalArgumentException("Entry not found.");
        }
        return item;
    }
    
    

    // -- Searches media items by title or partial title
    public List<Collection> findByTitle(String titlePart) {
        List<Collection> result = new ArrayList<>();
        for (Collection c : items) {
            if (c.getTitle().toLowerCase().contains(titlePart.toLowerCase())) {
                result.add(c);
            }
        }
        return result;
    }
    
    /**
     * Filters a list of media items by category.
     *
     * This method performs a non-destructive filter by creating
     * a defensive copy of the provided list before applying
     * category-based filtering.
     *
     * @param source   the source list of Collection objects to filter
     * @param category the category to filter by (Movie, Show, Documentary, or "All")
     * @return a new list containing only items that match the given category
     */
    public List<Collection> filterByCategory(List<Collection> source, String category) {

        // Defensive copy so the original list is not modified
        List<Collection> filtered = new ArrayList<>(source);

        // If no category is specified or "All" is selected,
        // return the full list without filtering
        if (category == null || category.equalsIgnoreCase("All")) {
            return filtered;
        }

        // Remove items that do NOT match the selected category (case-insensitive)
        filtered.removeIf(c ->
            !c.getCategory().equalsIgnoreCase(category)
        );

        // Return the filtered result list
        return filtered;
    }

    
    /**
     * Returns a sorted copy of the provided collection list based on the given sort key.
     *
     * This overloaded version allows sorting of any supplied list (e.g. filtered or searched results)
     * without modifying the original source list.
     *
     * parameter sortKey Integer representing the selected sort option
     *parameter source  The list of Collection objects to be sorted
     * parameter A new List containing the sorted results
     */
    public List<Collection> getSorted(int sortKey, List<Collection> source) {

        // Create a defensive copy of the PROVIDED list
        // This ensures the original list (source) remains unchanged
        List<Collection> copy = new ArrayList<>(source);

        // Retrieve the sorting option mapped to the given sort key
        String option = sortOptions.get(sortKey);

        // If no valid sort option exists, return the unsorted copy
        if (option == null) return copy;

        // Apply sorting logic based on the selected option
        switch (option) {

            // Sort by title alphabetically (A–Z), ignoring case
            case "TITLE_ASC" ->
                copy.sort(Comparator.comparing(
                    Collection::getTitle,
                    String.CASE_INSENSITIVE_ORDER
                ));

            // Sort by title in reverse alphabetical order (Z–A)
            case "TITLE_DESC" ->
                copy.sort(Comparator.comparing(
                    Collection::getTitle,
                    String.CASE_INSENSITIVE_ORDER
                ).reversed());

            // Sort by release year in ascending order
            case "YEAR_ASC" ->
                copy.sort(Comparator.comparingInt(Collection::getYear));

            // Sort by release year in descending order
            case "YEAR_DESC" ->
                copy.sort(Comparator.comparingInt(Collection::getYear).reversed());

            // Sort by rating from highest to lowest
            case "RATING_DESC" ->
                copy.sort(Comparator.comparingDouble(Collection::getRating).reversed());

            // Sort by rating from lowest to highest
            case "RATING_ASC" ->
                copy.sort(Comparator.comparingDouble(Collection::getRating));

            // Sort by number of views from highest to lowest
            case "VIEWS_DESC" ->
                copy.sort(Comparator.comparingInt(Collection::getViews).reversed());

            // Sort by number of views from lowest to highest
            case "VIEWS_ASC" ->
                copy.sort(Comparator.comparingInt(Collection::getViews));

            // Sort by category alphabetically (A–Z), ignoring case
            case "CATEGORY_ASC" ->
                copy.sort(Comparator.comparing(
                    Collection::getCategory,
                    String.CASE_INSENSITIVE_ORDER
                ));
        }

        // Return the sorted list copy
        return copy;
    }

    /**
     * Convenience overload that sorts the master collection list.
     *
     * This method delegates sorting to the main getSorted method
     * while keeping the original items list protected.
     *
     * parameter sortKey Integer representing the selected sort option
     * parameter A sorted copy of the master collection
     */
    public List<Collection> getSorted(int sortKey) {
        return getSorted(sortKey, items);
    }

    
    // ==============================
    // -- Add media
    // ==============================
    
    // -- Adds a new Movie to the collection
    public void addMovie(String title, String genre, int year, double rating, String subtype) {
        Movie m = new Movie(nextId++, title, genre, year, rating, subtype);
        addItem(m);
    }

    // -- Adds a new Show to the collection
    public void addShow(String title, String genre, int year, double rating, int seasons, int episodes) {
        Show s = new Show(nextId++, title, genre, year, rating, seasons, episodes);
        addItem(s);
    }

    // -- Adds a new Documentary to the collection
    public void addDocumentary(String title, String genre, int year, double rating, String subject) {
        Documentary d = new Documentary(nextId++, title, genre, year, rating, subject);
        addItem(d);
    }

    // -- Adds a media item to both the list and the map (for fast lookup)
    private void addItem(Collection c) {
        items.add(c);
        indexById.put(c.getId(), c);
    }
    
    // -- Adds a specific number of views to a media item
    public void addViews(int id, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Views cannot be negative.");
        }

        Collection c = getById(id);
        // -- Increments the 'views' counter multiple times based on the count
        for (int i = 0; i < count; i++) {
            c.addView();
        }
    }

    // -- Deletes a media item by its ID
    public boolean delete(int id) {
        Collection c = indexById.remove(id);
        if (c != null) {
            items.remove(c);
            return true;
        }
        return false;
    }
    
    // ==============================
    // -- Update media
    // ==============================
    
    //update movie type and ranking
    public void updateMovie(int id, double rating, String movieType) {
        Collection c = getById(id);

        if (!(c instanceof Movie m)) {
            throw new IllegalArgumentException("Item is not a Movie");
        }

        m.setRating(rating);
        m.setMovieType(movieType);
    }
    
    //update subject and ranking
    public void updateDocumentary(int id, double rating, String subject) {
        Collection c = getById(id);

        if (!(c instanceof Documentary d)) {
            throw new IllegalArgumentException("Item is not a Documentary");
        }

        d.setRating(rating);
        d.setSubject(subject);
    }

    //update seasons, episodes, and ranking
    public void updateShow(int id, double rating, int seasons, int episodes) {
        Collection c = getById(id);

        if (!(c instanceof Show s)) {
            throw new IllegalArgumentException("Item is not a Show");
        }

        s.setRating(rating);
        s.setSeasons(seasons);
        s.setEpisodes(episodes);
    }


    // ==============================
    // -- Evaluations (top 3)
    // ==============================
    
    // -- Finds the top 3 movies (by rating and views) for a given year
    public List<Movie> getTopMoviesByYear(int year) {

        List<Movie> movies = new ArrayList<>();

        // -- Collects only movies from a given year
        for (Collection c : items) {
            if (c instanceof Movie m && m.getYear() == year) {
                movies.add(m);
            }
        }

        // -- Sort the movies list: 
        // 1. Highest rating first 
        // 2. Then most viewed 
        // 3. Then lowest ID (to stabilize ordering)
        movies.sort(
            Comparator
                .comparingDouble(Movie::getRating).reversed()
                .thenComparing(Comparator.comparingInt(Movie::getViews).reversed())
                .thenComparingInt(Movie::getId)
        );

        // -- Return only the top 3 movies
        return movies.stream().limit(3).toList();
    }


    // -- Finds the top 3 most viewed shows
    public List<Show> evaluateTopShowsByViews() {

        List<Show> shows = new ArrayList<>();

        for (Collection c : items) {
            if (c instanceof Show s) {
                shows.add(s);
            }
        }

        // -- Sort the shows list: 
        // 1. Most viewed first 
        // 2. Then by highest rating
        // 3. Then by ID
        shows.sort(
            Comparator
                .comparingInt(Show::getViews).reversed()
                .thenComparing(Comparator.comparingDouble(Show::getRating).reversed())
                .thenComparingInt(Show::getId)
        );

        // -- Return only the top N shows (e.g. top 3) from the sorted list, converting the stream back into a list
        return shows.stream().limit(TOP_N).toList();
    }

    // -- Finds the top 3 documentaries with the highest ratings
    public List<Documentary> evaluateTopDocumentariesByRating() {

        List<Documentary> docs = new ArrayList<>();

        for (Collection c : items) {
            if (c instanceof Documentary d) {
                docs.add(d);
            }
        }
        
        // -- Sort the documentary list: 
        // 1. Highest rating first 
        // 2. Then most viewed
        // 3. Then lowest ID
        docs.sort(
            Comparator
                .comparingDouble(Documentary::getRating).reversed()
                .thenComparing(Comparator.comparingInt(Documentary::getViews).reversed())
                .thenComparingInt(Documentary::getId)
        );

        return docs.stream().limit(TOP_N).toList();
    }

    // ==============================
    // -- File Operations (Load&Save)
    // ==============================

    // -- Loads collection data from a CSV file into memory
    public void loadFromFile(String path) throws IOException {

        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found.");
        }

        // -- Clear any old data before loading new data
        items.clear();
        indexById.clear();

        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line;
            int maxId = 0;

            r.readLine(); // -- Skip header

            // -- Read each row of the CSV file
            while ((line = r.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] p = line.split(",", -1);

                int id = Integer.parseInt(p[0]);
                String title = p[1];
                String category = p[2].toUpperCase();
                String subtype = p[3];
                String genre = p[4];
                int year = Integer.parseInt(p[5]);
                double rating = Double.parseDouble(p[6]);
                int views = Integer.parseInt(p[7]);

                Collection c;

                // -- Create the correct object type depending on the category column
                switch (category) {
                    case "MOVIE" ->
                        c = new Movie(id, title, genre, year, rating, subtype);

                    case "SHOW" ->
                        c = new Show(id, title, genre, year, rating,
                                Integer.parseInt(p[8]),
                                Integer.parseInt(p[9]));

                    case "DOCUMENTARY" ->
                        c = new Documentary(id, title, genre, year, rating, p[8]);

                    default -> throw new IllegalArgumentException("Unknown category");
                }

                // -- Add pre-existing views from the file
                for (int i = 0; i < views; i++) c.addView();

                items.add(c);
                indexById.put(id, c);
                maxId = Math.max(maxId, id);
            }

            // -- Update next ID so new items don’t overwrite existing ones
            nextId = maxId + 1;
        }
    }

    // -- Saves all current media data into a CSV file
    public void saveToFile(String path) throws IOException {

        try (BufferedWriter w = new BufferedWriter(new FileWriter(path))) {
            w.write("id,title,category,subtype,genre,year,rating,views,extra1,extra2");
            w.newLine();

            // -- Go through each media item and write it as a CSV line
            for (Collection c : items) {

            	// -- If the item is a Movie
                if (c instanceof Movie m) {
                    w.write(m.getId() + "," + m.getTitle() + ",MOVIE," +
                            m.getMovieType() + "," + m.getGenre() + "," +
                            m.getYear() + "," + m.getRating() + "," +
                            m.getViews() + ",,");
                }

                // -- If the item is a Show (includes seasons and episodes)
                else if (c instanceof Show s) {
                    w.write(s.getId() + "," + s.getTitle() + ",SHOW,," +
                            s.getGenre() + "," + s.getYear() + "," +
                            s.getRating() + "," + s.getViews() + "," +
                            s.getSeasons() + "," + s.getEpisodes());
                }

                // -- If the item is a Documentary (includes subject)
                else if (c instanceof Documentary d) {
                    w.write(d.getId() + "," + d.getTitle() + ",DOCUMENTARY,," +
                            d.getGenre() + "," + d.getYear() + "," +
                            d.getRating() + "," + d.getViews() + "," +
                            d.getSubject() + ",");
                }

                // -- Move to new line for the next record  
                w.newLine();
            }
        }
    }
}

