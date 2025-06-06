package org.JBR.Sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * SQLite 
 */
public class SQLiteHelper {
    private final HikariDataSource dataSource;
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public SQLiteHelper(String dbPath) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + dbPath);
        config.setMaximumPoolSize(10);
        this.dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        Connection conn = dataSource.getConnection();
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }

    /**
     * 关闭数据库连接
     */
    public void close() {
       //Error! Exception Function
    }

    /**
     * 执行更新操作（INSERT/UPDATE/DELETE）
     * @param sql SQL语句
     * @param params 参数列表
     * @return 受影响的行数
     * @throws SQLException 如果执行出错
     */
    public int executeUpdate(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            setParameters(pstmt, params);
            return pstmt.executeUpdate();
        }
    }

    /**
     * 执行查询操作
     * @param sql SQL查询语句
     * @param params 参数列表
     * @return 结果列表（每行一个Map）
     * @throws SQLException 如果执行出错
     */
    public List<Map<String, Object>> executeQuery(String sql, Object... params) throws SQLException {
        List<Map<String, Object>> resultList = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            setParameters(pstmt, params);
            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnName(i), rs.getObject(i));
                    }
                    resultList.add(row);
                }
            }
        }
        return resultList;
    }

    /**
     * 执行事务
     * @param operations 要在事务中执行的操作
     * @return 是否成功
     */
    public boolean executeTransaction(SQLTransaction operations) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            
            // 执行事务操作
            boolean success = operations.execute(conn);
            
            if (success) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                System.err.println("Rollback error: " + ex.getMessage());
            }
            System.err.println("Transaction execution error: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                System.err.println("Reset auto-commit error: " + e.getMessage());
            }
        }
    }

    /**
     * 批量执行更新操作
     * @param sql SQL语句
     * @param batchParams 批量参数列表
     * @return 每批操作影响的行数
     * @throws SQLException 如果执行出错
     */
    public int[] executeBatchUpdate(String sql, List<Object[]> batchParams) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (Object[] params : batchParams) {
                setParameters(pstmt, params);
                pstmt.addBatch();
            }
            return pstmt.executeBatch();
        }
    }

    // 设置PreparedStatement参数
    private void setParameters(PreparedStatement pstmt, Object... params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
        }
    }

    public static String calculateDueDate(String borrowDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(borrowDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, 30); //* Assuming 30 days borrowing period */
            return sdf.format(calendar.getTime());
        } catch (ParseException e) {
            System.err.println("Error parsing date: " + e.getMessage());
            return null;
        }
    }

    public int executeQueryForSingleInt(String sql, Object... params) throws SQLException {
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        setParameters(stmt, params);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
}

    /**
     * 事务操作接口
     */
    @FunctionalInterface
    public interface SQLTransaction {
        boolean execute(Connection conn) throws SQLException;
    }
}