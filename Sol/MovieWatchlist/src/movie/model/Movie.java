package movie.model;

public class Movie {
    private String title;
    private String genre;
    private int year;
    private boolean watched;
    private int rating; // 0 = unrated, 1-10 stars

    public Movie(String title, String genre, int year) {
        if (title == null || title.trim().isEmpty())
            throw new IllegalArgumentException("Title cannot be empty.");
        if (year < 1888 || year > 2100)
            throw new IllegalArgumentException("Invalid year: " + year);
        this.title  = title.trim();
        this.genre  = genre;
        this.year   = year;
        this.watched = false;
        this.rating  = 0;
    }

    public String  getTitle()   { return title; }
    public String  getGenre()   { return genre; }
    public int     getYear()    { return year; }
    public boolean isWatched()  { return watched; }
    public int     getRating()  { return rating; }

    public void setWatched(boolean watched) { this.watched = watched; }

    public void setRating(int rating) {
        if (rating < 1 || rating > 10)
            throw new IllegalArgumentException("Rating must be between 1 and 10.");
        this.rating = rating;
    }

    @Override
    public String toString() {
        String stars   = rating > 0 ? " | " + "★".repeat(rating) : " | Unrated";
        String status  = watched ? " | Watched" : " | Unwatched";
        return title + " (" + year + ") | " + genre + stars + status;
    }
}
