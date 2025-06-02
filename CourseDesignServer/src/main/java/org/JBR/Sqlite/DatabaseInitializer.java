package org.JBR.Sqlite;

import java.sql.SQLException;
public class DatabaseInitializer {
    private static final String DB_PATH = "library.db";

    public static String getDatabasePath() {
        return DB_PATH;
    }

    public static void initializeDatabase() {
        SQLiteHelper dbHelper = new SQLiteHelper(DB_PATH);
        
        try {
            // 创建用户表
            dbHelper.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    user_id TEXT PRIMARY KEY,
                    username TEXT NOT NULL,
                    password_hash TEXT NOT NULL,
                    role TEXT NOT NULL,
                    frozen BOOLEAN DEFAULT FALSE
                )""");

            // 创建图书表
            dbHelper.executeUpdate("""
                CREATE TABLE IF NOT EXISTS books (
                    book_id TEXT PRIMARY KEY,
                    title TEXT NOT NULL,
                    author TEXT NOT NULL,
                    isbn TEXT UNIQUE,
                    available BOOLEAN DEFAULT TRUE
                )""");

            // 创建借阅记录表
            dbHelper.executeUpdate("""
                CREATE TABLE IF NOT EXISTS borrow_records (
                    record_id TEXT PRIMARY KEY,
                    borrow_date TEXT NOT NULL,
                    due_date TEXT NOT NULL,
                    return_date TEXT,
                    status TEXT CHECK(status IN ('BORROWED', 'RETURNED', 'OVERDUE')),
                    user_id TEXT NOT NULL,
                    book_id TEXT NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(user_id),
                    FOREIGN KEY (book_id) REFERENCES books(book_id)
                )""");

            System.out.println("数据库表创建成功");
         
            
        } catch (SQLException e) {
            System.err.println("数据库初始化失败: " + e.getMessage());
        } finally {
            dbHelper.close();
        }
    }

}