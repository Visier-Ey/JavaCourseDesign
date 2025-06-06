package org.visier.coursedesign.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;
import org.visier.coursedesign.Service.BorrowRecordService;
import org.visier.coursedesign.Service.UserService;
import org.visier.coursedesign.Session.UserSession;
import org.visier.coursedesign.Utils.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DashboardController {
    @FXML private Button statsButton;
    @FXML private VBox adminChartContainer;
    @FXML private VBox userChartContainer;
    @FXML private BarChart<String, Number> barChart;
    @FXML private PieChart pieChart;
    @FXML private VBox carouselContainer;
    @FXML private VBox leftBook;
    @FXML private VBox centerBook;
    @FXML private VBox rightBook;
    @FXML private PieChart userPieChart;

    private List<BookRecommendation> recommendedBooks = new ArrayList<>();
    private int currentStartIndex = 0;

    @FXML
    private void initialize() {
        statsButton.setOnAction(event -> toggleStatistics());
    }

    private void toggleStatistics() {
        if(UserSession.getCurrentUser().getRole().equals("ADMIN")) {
            adminChartContainer.setVisible(true);
            adminChartContainer.setManaged(true);
            userChartContainer.setVisible(false);
            userChartContainer.setManaged(false);
            loadStatistics();
            statsButton.setVisible(false);
        } else {
            adminChartContainer.setManaged(false);
            adminChartContainer.setVisible(false);
            userChartContainer.setManaged(true);
            userChartContainer.setVisible(true);
            loadRecommendedBooks();
            statsButton.setVisible(false);
        }
    }

    private void loadStatistics() {
        try {
            JSONObject response = BorrowRecordService.getBorrowStatistics();
            if (response == null || !response.getBoolean("success")) {
                Utils.showErrorAlert("Failed to load statistics", 
                    response != null ? response.getString("message") : "No response from server");
                return;
            }

            JSONArray statsArray = response.getJSONArray("statistics");
            List<BookStat> bookStats = new ArrayList<>();
            
            for (int i = 0; i < statsArray.length(); i++) {
                JSONObject stat = statsArray.getJSONObject(i);
                bookStats.add(new BookStat(
                    stat.getString("title"),
                    stat.getString("author"),
                    stat.getInt("total_borrows")
                ));
            }

            bookStats.sort(Comparator.comparingInt(BookStat::getBorrowCount).reversed());
            updateBarChart(bookStats);
            updatePieChart(bookStats, pieChart);

        } catch (Exception e) {
            e.printStackTrace();
            Utils.showErrorAlert("Error loading statistics", "An error occurred while loading statistics.");}
    }

    private void updateBarChart(List<BookStat> bookStats) {
        barChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Borrow Count");

        int displayCount = Math.min(5, bookStats.size());
        for (int i = 0; i < displayCount; i++) {
            BookStat stat = bookStats.get(i);
            String label = String.format("%s (%s)", shortenTitle(stat.getTitle()), stat.getAuthor());
            series.getData().add(new XYChart.Data<>(label, stat.getBorrowCount()));
        }

        barChart.getData().add(series);
        barChart.setTitle("Top Borrowed Books");
    }

    private void updatePieChart(List<BookStat> bookStats,PieChart pieChart) {
        pieChart.getData().clear();
        int total = bookStats.stream().mapToInt(BookStat::getBorrowCount).sum();

        int displayCount = Math.min(5, bookStats.size());
        for (int i = 0; i < displayCount; i++) {
            BookStat stat = bookStats.get(i);
            double percentage = (double) stat.getBorrowCount() / total * 100;
            String label = String.format("%s - %s (%.1f%%)", 
                shortenTitle(stat.getTitle()), 
                stat.getAuthor(), 
                percentage);
            pieChart.getData().add(new PieChart.Data(label, stat.getBorrowCount()));
        }

        if (bookStats.size() > 5) {
            int othersCount = bookStats.stream()
                    .skip(5)
                    .mapToInt(BookStat::getBorrowCount)
                    .sum();
            double othersPercentage = (double) othersCount / total * 100;
            pieChart.getData().add(new PieChart.Data(
                    String.format("Others (%.1f%%)", othersPercentage),
                    othersCount));
        }

        pieChart.setTitle("Your Borrow Distribution");
    }

    private void loadRecommendedBooks() {
        try {
            JSONObject response = UserService.getUserRecommendations();

            if (response != null && response.getBoolean("success")) {
                JSONObject booksArray = response.getJSONObject("recommendations");
                JSONArray popular_books = booksArray.getJSONArray("popular_books");
                JSONArray user_books = booksArray.getJSONArray("user_books");
                for (int i = 0; i < popular_books.length(); i++) {
                    JSONObject book = popular_books.getJSONObject(i);
                    recommendedBooks.add(new BookRecommendation(
                        book.getString("title"),
                        book.getString("author"),
                        book.getDouble("percentage")
                    ));
                }
                List<BookStat> bookStats = new ArrayList<>();
                for (int i = 0; i < user_books.length(); i++) {
                    JSONObject book = user_books.getJSONObject(i);
                    bookStats.add(new BookStat(
                        book.getString("title"),
                        book.getString("author"),
                        (int)(book.getDouble("percentage"))
                    ));
                }
                updateCarousel(0);
                updatePieChart(bookStats,userPieChart);
            } else {
                Utils.showErrorAlert("Failed to load recommendations", response.getString("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCarousel(int startIndex) {
        if (recommendedBooks.isEmpty()) return;
        
        BookRecommendation left = recommendedBooks.get(
            (startIndex) % recommendedBooks.size());
        BookRecommendation center = recommendedBooks.get(
            (startIndex + 1) % recommendedBooks.size());
        BookRecommendation right = recommendedBooks.get(
            (startIndex + 2) % recommendedBooks.size());
        
        updateBookView(leftBook, left, false);
        updateBookView(centerBook, center, true);
        updateBookView(rightBook, right, false);
    }

    private void updateBookView(VBox container, BookRecommendation book, boolean isCenter) {
        container.getChildren().clear();
        
        
        
    
        Label titleLabel = new Label(book.getTitle());
        titleLabel.setStyle("-fx-font-size: " + (isCenter ? "16px" : "14px") + 
                          "; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        Label authorLabel = new Label(book.getAuthor());
        authorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        
        if (isCenter) {
            Label ratingLabel = new Label(String.format("⭐ %.1f/5", book.getRating()));
            ratingLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #ff9900;");
            container.getChildren().addAll( titleLabel, authorLabel, ratingLabel);
        } else {
            container.getChildren().addAll( titleLabel, authorLabel);
        }
        
        container.setStyle("-fx-padding: " + (isCenter ? "0 15 0 15" : "0 10 20 10") + 
                        "; -fx-effect: dropshadow(gaussian, rgba(0,0,0," + 
                        (isCenter ? "0.3" : "0.2") + "), " + 
                        (isCenter ? "10" : "5") + ", 0.3, 0, " + 
                        (isCenter ? "4" : "2") + ");");
    }

    @FXML
    private void previousCarouselItem(ActionEvent event) {
        currentStartIndex = (currentStartIndex - 1 + recommendedBooks.size()) % recommendedBooks.size();
        updateCarousel(currentStartIndex);
    }

    @FXML
    private void nextCarouselItem(ActionEvent event) {
        currentStartIndex = (currentStartIndex + 1) % recommendedBooks.size();
        updateCarousel(currentStartIndex);
    }

    private String shortenTitle(String title) {
        return title.length() > 10 ? title.substring(0, 10) + "..." : title;
    }

    private static class BookStat {
        private final String title;
        private final String author;
        private final int borrowCount;

        public BookStat(String title, String author, int borrowCount) {
            this.title = title;
            this.author = author;
            this.borrowCount = borrowCount;
        }

        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public int getBorrowCount() { return borrowCount; }
    }

    private static class BookRecommendation {
        private final String title;
        private final String author;
        private final double rating;
        
        public BookRecommendation(String title, String author, double rating) {
            this.title = title;
            this.author = author;
            this.rating = rating;
        }

        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public double getRating() { return rating; }
    }
}