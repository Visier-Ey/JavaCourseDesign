package org.JBR.DAO;

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
            System.err.println("添加图书失败: " + e.getMessage());
            return false;
        }
    }

    // 查询所有图书
    public List<Map<String, Object>> getAllBooks() {
        try {
            return dbHelper.executeQuery("SELECT * FROM books");
        } catch (SQLException e) {
            System.err.println("查询图书失败: " + e.getMessage());
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
            System.err.println("更新图书状态失败: " + e.getMessage());
            return false;
        }
    }
}