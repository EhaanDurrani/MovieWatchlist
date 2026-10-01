package movie.ui;

import movie.model.Movie;
import movie.service.WatchlistService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class WatchlistUI extends JFrame {

    private WatchlistService service = new WatchlistService();
    private DefaultTableModel tableModel;

    private JTextField titleField, genreField, yearField;
    private JTextField actionTitleField;
    private JComboBox<String> ratingBox, filterBox;
    private JLabel statusLabel, statsLabel;

    public WatchlistUI() {
        setTitle("Movie Watchlist Manager");
        setSize(850, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(buildAddPanel(),    BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildStatusBar(),   BorderLayout.SOUTH);

        setVisible(true);
    }

    // ── Add Movie Panel ──────────────────────────────────────────────
    private JPanel buildAddPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Add Movie"));
        panel.setBackground(new Color(230, 240, 255));

        titleField = new JTextField(14);
        genreField = new JTextField(10);
        yearField  = new JTextField(5);

        panel.add(new JLabel("Title:"));  panel.add(titleField);
        panel.add(new JLabel("Genre:"));  panel.add(genreField);
        panel.add(new JLabel("Year:"));   panel.add(yearField);

        JButton addBtn = makeButton("Add Movie", new Color(0, 102, 204));
        addBtn.addActionListener(e -> handleAddMovie()); // Event Handling
        panel.add(addBtn);
        return panel;
    }

    // ── Center: Table + Actions ──────────────────────────────────────
    private JPanel buildCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(buildTablePanel(),  BorderLayout.CENTER);
        panel.add(buildActionPanel(), BorderLayout.EAST);
        return panel;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("My Watchlist"));

        String[] cols = {"Title", "Genre", "Year", "Watched", "Rating"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(24);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Filter row
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterBox = new JComboBox<>(new String[]{"All", "Watched", "Unwatched"});
        JButton filterBtn = makeButton("Filter", new Color(100, 100, 200));
        filterBtn.addActionListener(e -> handleFilter()); // Event Handling
        filterRow.add(new JLabel("Show:"));
        filterRow.add(filterBox);
        filterRow.add(filterBtn);
        panel.add(filterRow, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildActionPanel() {
        JPanel panel = new JPanel(new GridLayout(10, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Actions"));
        panel.setPreferredSize(new Dimension(200, 0));

        actionTitleField = new JTextField();
        ratingBox = new JComboBox<>(new String[]{"1","2","3","4","5","6","7","8","9","10"});

        panel.add(new JLabel("Movie Title:"));
        panel.add(actionTitleField);

        JButton watchedBtn = makeButton("Mark Watched",   new Color(0, 153, 51));
        JButton unwatchBtn = makeButton("Mark Unwatched", new Color(180, 100, 0));
        JButton deleteBtn  = makeButton("Delete Movie",   new Color(200, 0, 0));

        watchedBtn.addActionListener(e -> handleMarkWatched(true));  // Event Handling
        unwatchBtn.addActionListener(e -> handleMarkWatched(false)); // Event Handling
        deleteBtn.addActionListener(e -> handleDelete());            // Event Handling

        panel.add(watchedBtn);
        panel.add(unwatchBtn);
        panel.add(new JSeparator());
        panel.add(new JLabel("Rate (1-10):"));
        panel.add(ratingBox);

        JButton rateBtn = makeButton("Rate Movie", new Color(100, 0, 180));
        rateBtn.addActionListener(e -> handleRate()); // Event Handling
        panel.add(rateBtn);

        panel.add(new JSeparator());
        statsLabel = new JLabel("<html>Stats loading...</html>");
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        panel.add(statsLabel);

        return panel;
    }

    private JPanel buildStatusBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Welcome to Movie Watchlist Manager!");
        statusLabel.setForeground(new Color(0, 100, 0));
        panel.add(statusLabel);
        return panel;
    }

    // ── Event Handlers ────────────────────────────────────────────────
    private void handleAddMovie() {
        try {
            String title = titleField.getText().trim();
            String genre = genreField.getText().trim();
            int    year  = Integer.parseInt(yearField.getText().trim());
            if (genre.isEmpty()) genre = "Unknown";
            service.addMovie(title, genre, year);
            setStatus("Added: " + title, true);
            clearAddFields();
            refreshTable(service.getAllMovies());
        } catch (NumberFormatException ex) {
            showError("Year must be a number."); // Exception Handling
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());           // Exception Handling
        }
    }

    private void handleMarkWatched(boolean watched) {
        try {
            String title = actionTitleField.getText().trim();
            service.markWatched(title, watched);
            setStatus(title + (watched ? " marked as watched." : " marked as unwatched."), true);
            refreshTable(service.getAllMovies());
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage()); // Exception Handling
        }
    }

    private void handleRate() {
        try {
            String title  = actionTitleField.getText().trim();
            int    rating = Integer.parseInt((String) ratingBox.getSelectedItem());
            service.rateMovie(title, rating);
            setStatus(title + " rated " + rating + " stars.", true);
            refreshTable(service.getAllMovies());
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage()); // Exception Handling
        }
    }

    private void handleDelete() {
        try {
            String title = actionTitleField.getText().trim();
            int confirm  = JOptionPane.showConfirmDialog(this,
                "Delete '" + title + "' from watchlist?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                service.removeMovie(title);
                setStatus("Deleted: " + title, true);
                refreshTable(service.getAllMovies());
            }
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage()); // Exception Handling
        }
    }

    private void handleFilter() {
        String selected = (String) filterBox.getSelectedItem();
        List<Movie> filtered;
        if ("Watched".equals(selected))        filtered = service.filterByStatus(true);
        else if ("Unwatched".equals(selected)) filtered = service.filterByStatus(false);
        else                                    filtered = service.getAllMovies();
        refreshTable(filtered);
        setStatus("Showing: " + selected + " (" + filtered.size() + " movies)", true);
    }

    // ── Refactored Helper Methods ─────────────────────────────────────
    private void refreshTable(List<Movie> list) {
        tableModel.setRowCount(0);
        for (Movie m : list) {
            tableModel.addRow(new Object[]{
                m.getTitle(), m.getGenre(), m.getYear(),
                m.isWatched() ? "Yes" : "No",
                m.getRating() > 0 ? m.getRating() + " stars" : "Unrated"
            });
        }
        updateStats();
    }

    private void updateStats() {
        long watched = service.getAllMovies().stream().filter(Movie::isWatched).count();
        double avg   = service.getAverageRating();
        String fav   = service.getFavoriteGenre();
        statsLabel.setText("<html>Total: " + service.getAllMovies().size() +
            "<br>Watched: " + watched +
            "<br>Avg Rating: " + String.format("%.1f", avg) +
            "<br>Fav Genre: " + fav + "</html>");
    }

    private JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    private void clearAddFields() {
        titleField.setText("");
        genreField.setText("");
        yearField.setText("");
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
        setStatus("Error: " + msg, false);
    }

    private void setStatus(String msg, boolean ok) {
        statusLabel.setText(msg);
        statusLabel.setForeground(ok ? new Color(0, 128, 0) : Color.RED);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WatchlistUI::new);
    }
}
