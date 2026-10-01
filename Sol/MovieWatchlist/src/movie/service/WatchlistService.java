package movie.service;

import movie.model.Movie;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WatchlistService {

    private List<Movie> movies = new ArrayList<>();

    public Movie addMovie(String title, String genre, int year) {
        boolean duplicate = movies.stream()
            .anyMatch(m -> m.getTitle().equalsIgnoreCase(title.trim()));
        if (duplicate)
            throw new IllegalArgumentException("Movie already exists: " + title);
        Movie movie = new Movie(title, genre, year);
        movies.add(movie);
        return movie;
    }

    public void removeMovie(String title) {
        Movie m = findByTitle(title);
        movies.remove(m);
    }

    public void markWatched(String title, boolean watched) {
        findByTitle(title).setWatched(watched);
    }

    public void rateMovie(String title, int rating) {
        findByTitle(title).setRating(rating);
    }

    public List<Movie> getAllMovies() { return movies; }

    public List<Movie> filterByGenre(String genre) {
        return movies.stream()
            .filter(m -> m.getGenre().equalsIgnoreCase(genre))
            .collect(Collectors.toList());
    }

    public List<Movie> filterByStatus(boolean watched) {
        return movies.stream()
            .filter(m -> m.isWatched() == watched)
            .collect(Collectors.toList());
    }

    public double getAverageRating() {
        return movies.stream()
            .filter(m -> m.getRating() > 0)
            .mapToInt(Movie::getRating)
            .average().orElse(0.0);
    }

    public String getFavoriteGenre() {
        return movies.stream()
            .filter(m -> m.getRating() > 0)
            .collect(Collectors.groupingBy(Movie::getGenre,
                     Collectors.averagingInt(Movie::getRating)))
            .entrySet().stream()
            .max(java.util.Map.Entry.comparingByValue())
            .map(java.util.Map.Entry::getKey)
            .orElse("N/A");
    }

    // Refactored helper — avoids duplication across methods
    private Movie findByTitle(String title) {
        return movies.stream()
            .filter(m -> m.getTitle().equalsIgnoreCase(title.trim()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Movie not found: " + title));
    }
}
