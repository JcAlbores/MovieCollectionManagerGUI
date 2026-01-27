package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import java.nio.file.Files;
import java.nio.file.Path;

import core.Collection;
import core.CollectionManager;

public class CollectionManagerTest {

    private CollectionManager manager;

    @BeforeEach
    void setUp() {
        manager = new CollectionManager();
    }

    // TC-U-01: Add Movie increases size
    @Test
    void addMovie_increasesCollectionSize() {
        manager.addMovie("Demon Slayer Infinity Castle", "Anime", 2025, 9.5, "Blockbuster");
        assertEquals(1, manager.getAll().size());
    }

    // TC-U-02: Add Show increases size
    @Test
    void addShow_increasesCollectionSize() {
        manager.addShow("Breaking Bad", "Drama", 2008, 9.5, 5, 62);
        assertEquals(1, manager.getAll().size());
    }

    // TC-U-03: Add Documentary increases size
    @Test
    void addDocumentary_increasesCollectionSize() {
        manager.addDocumentary("Planet Earth", "Nature", 2006, 9.4, "Wildlife");
        assertEquals(1, manager.getAll().size());
    }

    // TC-U-04: Search by exact title
    @Test
    void findByTitle_exactMatch_returnsResult() {
        manager.addMovie("Pulp Fiction", "Drama", 1994, 8.9, "Classic");

        List<Collection> result = manager.findByTitle("Pulp Fiction");

        assertEquals(1, result.size());
        assertEquals("Pulp Fiction", result.get(0).getTitle());
    }

    // TC-U-05: Search by partial title (case-insensitive)
    @Test
    void findByTitle_partialCaseInsensitive_returnsResult() {
        manager.addMovie("Inside Out", "Animated", 2015, 9.5, "Blockbuster");

        List<Collection> result = manager.findByTitle("iDe");

        assertEquals(1, result.size());
    }

    // TC-U-06: Search with no match returns empty list
    @Test
    void findByTitle_noMatch_returnsEmptyList() {
        manager.addMovie("Inception 2", "Sci-Fi", 2010, 9.0, "Short");

        List<Collection> result = manager.findByTitle("Test123");

        assertTrue(result.isEmpty());
    }

    // TC-U-07: Filter by category "All" returns full list
    @Test
    void filterByCategory_all_returnsAllItems() {
        manager.addMovie("Movie A", "Drama", 2020, 8.0, "Standard");
        manager.addDocumentary("Doc A", "History", 2019, 7.5, "War");

        List<Collection> filtered =
                manager.filterByCategory(manager.getAll(), "All");

        assertEquals(2, filtered.size());
    }

    // TC-U-08: Filter by category "Movie"
    @Test
    void filterByCategory_movie_returnsOnlyMovies() {
        manager.addMovie("Movie A", "Drama", 2020, 8.0, "Standard");
        manager.addDocumentary("Doc A", "History", 2019, 7.5, "War");

        List<Collection> filtered =
                manager.filterByCategory(manager.getAll(), "Movie");

        assertEquals(1, filtered.size());
        assertEquals("Movie", filtered.get(0).getCategory());
    }

    // TC-U-09: Sort by rating descending
    @Test
    void getSorted_ratingDesc_highestRatingFirst() {
        manager.addMovie("Low", "Drama", 2020, 5.0, "Standard");
        manager.addMovie("High", "Drama", 2021, 9.5, "Standard");

        List<Collection> sorted = manager.getSorted(5); // Sort Key 5 is RATING_DESC

        assertEquals("High", sorted.get(0).getTitle());
    }

    // TC-U-10: Binary search by ID returns correct item
    @Test
    void binarySearchById_existingId_returnsItem() {
        manager.addMovie("Inception 3", "Sci-Fi", 2010, 9.0, "Blockbuster");
        manager.addMovie("Avatar", "Sci-Fi", 2009, 8.5, "Standard");

        Collection result = manager.binarySearchById(2);

        assertNotNull(result);
        assertEquals("Avatar", result.getTitle());
    }
    
    // TC-U-11: Add multiple movies from same year with different ratings and views
    @Test
    void getTopMoviesByYear_returnsTopThreeMovies() {
        manager.addMovie("Movie A", "Drama", 2020, 8.0, "Classic");
        manager.addMovie("Movie B", "Drama", 2020, 9.5, "Classic");
        manager.addMovie("Movie C", "Drama", 2020, 7.0, "Classic");
        manager.addMovie("Movie D", "Drama", 2020, 9.0, "Classic");

        var topMovies = manager.getTopMoviesByYear(2020);

        assertEquals(3, topMovies.size());
        assertEquals("Movie B", topMovies.get(0).getTitle());
    }
    
    // TC-U-12: Add multiple shows with different view counts
    @Test
    void evaluateTopShowsByViews_ordersShowsCorrectly() {
        manager.addShow("Show A", "Drama", 2019, 8.0, 2, 20);
        manager.addShow("Show B", "Drama", 2019, 9.0, 3, 30);
        manager.addShow("Show C", "Drama", 2019, 7.0, 1, 10);

        manager.addViews(1, 5); // Show A
        manager.addViews(2, 20); // Show B
        manager.addViews(3, 10); // Show C

        var topShows = manager.evaluateTopShowsByViews();

        assertEquals(3, topShows.size());
        assertEquals("Show B", topShows.get(0).getTitle());
    }

    // TC-U-13: Add documentaries with different ratings
    @Test
    void evaluateTopDocumentariesByRating_returnsHighestRated() {
        manager.addDocumentary("Doc A", "History", 2018, 7.5, "War");
        manager.addDocumentary("Doc B", "Science", 2019, 9.0, "Space");
        manager.addDocumentary("Doc C", "Nature", 2020, 8.5, "Wildlife");

        var topDocs = manager.evaluateTopDocumentariesByRating();

        assertEquals(3, topDocs.size());
        assertEquals("Doc B", topDocs.get(0).getTitle());
    }

    // TC-U-14 and TC-U-15: Save collection to temporary CSV file and Load data from valid CSV file
    @Test
    void saveAndLoadFile_persistsCollectionCorrectly() throws Exception {
        manager.addMovie("Chainsaw Man Reze Arc", "Anime", 2025, 9.0, "Blockbuster");

        Path tempFile = Files.createTempFile("collection_test", ".csv");

        manager.saveToFile(tempFile.toString());

        CollectionManager newManager = new CollectionManager();
        newManager.loadFromFile(tempFile.toString());

        assertEquals(1, newManager.getAll().size());

        Files.deleteIfExists(tempFile); //delete the temporary file created for this test
    }
    
    // TC-U-16:Load from non-existent file
    @Test
    void loadFromFile_invalidPath_throwsException() {
        assertThrows(Exception.class, () -> {
            manager.loadFromFile("non_existent_file.csv");
        });
    }


}
