package org.JBR.DAO;

import org.JBR.Sqlite.SQLiteHelper;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

public class BorrowRecordDAO {
    private final SQLiteHelper dbHelper;

    public BorrowRecordDAO(String dbPath) {
        this.dbHelper = new SQLiteHelper(dbPath);
    }

    public void close() {
        dbHelper.close();
    }

    // * Get all borrow records */
    public List<Map<String, Object>> getAllBorrowRecords() {
        try {
            String query = "SELECT br.*, u.username, b.title as book_title " +
                    "FROM borrow_records br " +
                    "JOIN users u ON br.user_id = u.user_id " +
                    "JOIN books b ON br.book_id = b.book_id";
            return dbHelper.executeQuery(query);
        } catch (SQLException e) {
            System.err.println("Query All the Records failed: " + e.getMessage());
            return List.of();
        }
    }

    // * Get borrow records by user */
    public List<Map<String, Object>> getBorrowRecordsByUser(String userId) {
        try {
            String query = "SELECT br.*, u.username, b.title as book_title " +
                    "FROM borrow_records br " +
                    "JOIN users u ON br.user_id = u.user_id " +
                    "JOIN books b ON br.book_id = b.book_id " +
                    "WHERE br.user_id = ?";
            return dbHelper.executeQuery(query, userId);
        } catch (SQLException e) {
            System.err.println("Query Borrow Records by User failed: " + e.getMessage());
            return List.of();
        }
    }

    // * Add the user borrowRecord */
    public boolean addBorrowRecord(String userId, String bookId, String borrowDate) {
        try {
            // * Add the user borrowRecord */
            boolean success = dbHelper.executeTransaction(conn -> {
                // * 1.Insert the Record */
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO borrow_records (record_id, user_id, book_id, borrow_date, due_date, status) " +
                                "VALUES (?, ?, ?, ?, ?, ?)")) {
                    pstmt.setString(1, "R" + System.currentTimeMillis()); // Generate a unique record ID
                    pstmt.setString(2, userId);
                    pstmt.setString(3, bookId);
                    pstmt.setString(4, borrowDate);
                    pstmt.setString(5, SQLiteHelper.calculateDueDate(borrowDate)); // Assuming this method exists
                    pstmt.setString(6, "BORROWED");
                    pstmt.executeUpdate();
                }

                // * 2.Update the Book Status */
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "UPDATE books SET available = FALSE WHERE book_id = ?")) {
                    pstmt.setString(1, bookId);
                    pstmt.executeUpdate();
                }
                return true;
            });

            return success;

        } catch (Exception e) {
            System.err.println("Add Records Failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // * Update the borrow record */
    public boolean updateBorrowRecord(String recordId, String userId, String bookId, String borrowDate,
            String dueDate) {
        try {
            boolean success = dbHelper.executeTransaction(conn -> {
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "UPDATE borrow_records SET user_id = ?, book_id = ?, borrow_date = ?, due_date = ? WHERE record_id = ?")) {
                    pstmt.setString(1, userId);
                    pstmt.setString(2, bookId);
                    pstmt.setString(3, borrowDate);
                    pstmt.setString(4, dueDate);
                    pstmt.setString(5, recordId);
                    pstmt.executeUpdate();
                }
                return true;
            });
            return success;
        } catch (Exception e) {
            System.err.println("Update Records Failed: " + e.getMessage());
            return false;
        }
    }

    // * Delete the borrow record */
    public boolean deleteBorrowRecord(String recordId) {
        try {
            boolean success = dbHelper.executeTransaction(conn -> {
                // * 1.Delete the Record */
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "DELETE FROM borrow_records WHERE record_id = ?")) {
                    pstmt.setString(1, recordId);
                    pstmt.executeUpdate();
                }

                // * 2.Update the Book Status */
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "UPDATE books SET available = TRUE WHERE book_id = (SELECT book_id FROM borrow_records WHERE record_id = ?)")) {
                    pstmt.setString(1, recordId);
                    pstmt.executeUpdate();
                }
                return true;
            });
            return success;
        } catch (Exception e) {
            System.err.println("Delete Records Failed: " + e.getMessage());
            return false;
        }
    }

    public boolean returnBook(String recordId) {
        try {
            boolean success = dbHelper.executeTransaction(conn -> {
                //* 1. YYYY-MM-D */
                String returnDate = LocalDate.now().toString(); 

                try (PreparedStatement pstmt = conn.prepareStatement(
                        "UPDATE borrow_records SET status = 'RETURNED', return_date = ? WHERE record_id = ?")) {
                    pstmt.setString(1, returnDate);
                    pstmt.setString(2, recordId);
                    int updatedRecords = pstmt.executeUpdate();

                    if (updatedRecords == 0) {
                        throw new SQLException("未找到借阅记录ID: " + recordId);
                    }
                }

                //* 2. Update the Book Status */
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "UPDATE books SET available = TRUE WHERE book_id = " +
                                "(SELECT book_id FROM borrow_records WHERE record_id = ?)")) {
                    pstmt.setString(1, recordId);
                    pstmt.executeUpdate();
                }

                System.out.println("Return Book: " + recordId);
                return true;
            });
            return success;
        } catch (Exception e) {
            System.err.println("Return Book Failed: " + e.getMessage());
            return false;
        }
    }

    public List<Map<String, Object>> getBorrowStatistics() {
        try {
            String query = "SELECT b.book_id, b.title, b.author, " +
                    "COUNT(br.record_id) AS total_borrows " +
                    "FROM books b " +
                    "INNER JOIN borrow_records br ON b.book_id = br.book_id " +
                    "GROUP BY b.book_id " +
                    "ORDER BY total_borrows DESC";

            return new SQLiteHelper("library.db").executeQuery(query);
        } catch (SQLException e) {
            System.err.println("Get Borrow Statistics Failed: " + e.getMessage());
            return List.of();
        }
    }
}