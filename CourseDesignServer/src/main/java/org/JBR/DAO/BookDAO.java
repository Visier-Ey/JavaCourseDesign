package org.JBR.DAO;

import org.JBR.Sqlite.DatabaseInitializer;
import org.JBR.Sqlite.SQLiteHelper;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class BookDAO {
    private final SQLiteHelper dbHelper;

    public BookDAO(String dbPath) {
        this.dbHelper = new SQLiteHelper(dbPath);
    }

    public void close() {
        dbHelper.close();
    }

    // 添加图书
    public boolean addBook(String bookId, String title, String author, String isbn) {
        try {
            int affected = dbHelper.executeUpdate(
                    "INSERT INTO books (book_id, title, author, isbn) VALUES (?, ?, ?, ?)",
                    bookId, title, author, isbn);
            return affected > 0;
        } catch (SQLException e) {
            System.err.println("Add Book Failed: " + e.getMessage());
            return false;
        }
    }

    // 查询所有图书
    public List<Map<String, Object>> getAllBooks() {
        try {
            return dbHelper.executeQuery("SELECT * FROM books");
        } catch (SQLException e) {
            System.err.println("Get All Books Failed: " + e.getMessage());
            return List.of();
        }
    }

    // 更新图书可用状态
    public boolean updateBookAvailability(String bookId, boolean available) {
        try {
            int affected = dbHelper.executeUpdate(
                    "UPDATE books SET available = ? WHERE book_id = ?",
                    available, bookId);
            return affected > 0;
        } catch (SQLException e) {
            System.err.println("Update Book Availability Failed: " + e.getMessage());
            return false;
        }
    }

    // 删除图书
    public boolean deleteBook(String bookId) {
        SQLiteHelper dbHelper = new SQLiteHelper(DatabaseInitializer.getDatabasePath());
        try {
            // Check if the book has active borrow records
            List<Map<String, Object>> activeRecords = dbHelper.executeQuery(
                    "SELECT 1 FROM borrow_records WHERE book_id = ? AND status = 'BORROWED' LIMIT 1",
                    bookId);

            if (!activeRecords.isEmpty()) {
                System.err.println("Cannot delete book: There are active borrow records");
                return false;
            }

            // Delete related borrow records first
            dbHelper.executeUpdate(
                    "DELETE FROM borrow_records WHERE book_id = ?",
                    bookId);

            // Delete the book
            int affected = dbHelper.executeUpdate(
                    "DELETE FROM books WHERE book_id = ?",
                    bookId);

            return affected > 0;
        } catch (SQLException e) {
            System.err.println("Delete book failed: " + e.getMessage());
            return false;
        } finally {
            dbHelper.close();
        }
    }
}