package movie.test;

import movie.model.Movie;
import movie.service.WatchlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WatchlistServiceTest {

    private WatchlistService service;

    @BeforeEach
    void setUp() { service = new WatchlistService(); }

    @Test
    void testAddMovie_ValidInput() {
        Movie m = service.addMovie("Inception", "Sci-Fi", 2010);
        assertNotNull(m);
        assertEquals("Inception", m.getTitle());
        assertEquals(1, service.getAllMovies().size());
    }

    @Test
    void testAddMovie_Duplicate_ThrowsException() {
        service.addMovie("Inception", "Sci-Fi", 2010);
        assertThrows(IllegalArgumentException.class,
            () -> service.addMovie("Inception", "Sci-Fi", 2010));
    }

    @Test
    void testAddMovie_EmptyTitle_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> service.addMovie("", "Action", 2020));
    }

    @Test
    void testAddMovie_InvalidYear_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> service.addMovie("FutureFilm", "Sci-Fi", 1800));
    }

    @Test
    void testMarkWatched_UpdatesStatus() {
        service.addMovie("Dune", "Sci-Fi", 2021);
        service.markWatched("Dune", true);
        assertTrue(service.getAllMovies().get(0).isWatched());
    }

    @Test
    void testRateMovie_ValidRating() {
        service.addMovie("Interstellar", "Sci-Fi", 2014);
        service.rateMovie("Interstellar", 5);
        assertEquals(5, service.getAllMovies().get(0).getRating());
    }

    @Test
    void testRateMovie_InvalidRating_ThrowsException() {
        service.addMovie("Joker", "Drama", 2019);
        assertThrows(IllegalArgumentException.class,
            () -> service.rateMovie("Joker", 11));
    }

    @Test
    void testRemoveMovie_RemovesCorrectly() {
        service.addMovie("Matrix", "Sci-Fi", 1999);
        service.removeMovie("Matrix");
        assertEquals(0, service.getAllMovies().size());
    }

    @Test
    void testFilterByStatus_ReturnsCorrectMovies() {
        service.addMovie("A", "Action", 2000);
        service.addMovie("B", "Drama", 2001);
        service.markWatched("A", true);
        assertEquals(1, service.filterByStatus(true).size());
        assertEquals(1, service.filterByStatus(false).size());
    }

    @Test
    void testGetAverageRating_CalculatesCorrectly() {
        service.addMovie("X", "Action", 2010);
        service.addMovie("Y", "Drama", 2011);
        service.rateMovie("X", 4);
        service.rateMovie("Y", 2);
        assertEquals(3.0, service.getAverageRating(), 0.01);
    }
}
