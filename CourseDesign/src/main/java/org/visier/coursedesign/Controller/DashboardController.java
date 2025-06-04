package org.visier.coursedesign.Controller;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import org.json.JSONArray;
import org.json.JSONObject;
import org.visier.coursedesign.Service.BorrowRecordService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardController {
    @FXML
    private Button statsButton;
    @FXML
    private HBox chartContainer;
    @FXML
    private BarChart<String, Number> barChart;
    @FXML
    private PieChart pieChart;

    @FXML
    private void initialize() {
        statsButton.setOnAction(event -> toggleStatistics());
    }

    private void toggleStatistics() {
        if (chartContainer.isVisible()) {
            chartContainer.setVisible(false);
            statsButton.setText("View Statistics");
        } else {
            loadStatistics();
            chartContainer.setVisible(true);
            statsButton.setText("Hide Statistics");
        }
    }

    private void loadStatistics() {
    try {
        JSONObject response = BorrowRecordService.getBorrowStatistics();
        if (response == null || !response.getBoolean("success")) {
            System.err.println("Failed to retrieve borrow statistics");
            return;
        }

        // Process statistics data
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

        // Sort by borrow count descending
        bookStats.sort(Comparator.comparingInt(BookStat::getBorrowCount).reversed());

        // Update charts
        updateBarChart(bookStats);
        updatePieChart(bookStats);

    } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Error loading statistics: " + e.getMessage());
    }
}

    private void updateBarChart(List<BookStat> bookStats) {
        barChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Borrow Count");

        // Show top 5 books in bar chart
        int displayCount = Math.min(5, bookStats.size());
        for (int i = 0; i < displayCount; i++) {
            BookStat stat = bookStats.get(i);
            String label = String.format("%s (%s)", shortenTitle(stat.getTitle()), stat.getAuthor());
            series.getData().add(new XYChart.Data<>(label, stat.getBorrowCount()));
        }

        barChart.getData().add(series);
        barChart.setTitle("Top Borrowed Books");
    }

    private void updatePieChart(List<BookStat> bookStats) {
        pieChart.getData().clear();
        int total = bookStats.stream().mapToInt(BookStat::getBorrowCount).sum();

        // Show top 5 books in pie chart
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

        // Add Others category if there are more than 5 books
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

        pieChart.setTitle("Borrow Distribution");
    }

    private String shortenTitle(String title) {
        return title.length() > 10 ? title.substring(0, 10) + "..." : title;
    }

    // Internal class to store book statistics
    private static class BookStat {
        private final String title;
        private final String author;
        private final int borrowCount;

        public BookStat(String title, String author, int borrowCount) {
            this.title = title;
            this.author = author;
            this.borrowCount = borrowCount;
        }

        public String getTitle() {
            return title;
        }

        public String getAuthor() {
            return author;
        }

        public int getBorrowCount() {
            return borrowCount;
        }
    }
}