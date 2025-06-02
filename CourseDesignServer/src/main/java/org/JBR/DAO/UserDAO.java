package org.JBR.DAO;

import org.JBR.Sqlite.SQLiteHelper;
import org.mindrot.jbcrypt.BCrypt;
import java.util.List;
import java.util.Map;
import java.sql.SQLException;

/**
 * Data Access Object for user operations (backend only)
 */
public class UserDAO {
    private final SQLiteHelper dbHelper;
    private static final int BCRYPT_ROUNDS = 12;

    public UserDAO(String dbPath) {
        this.dbHelper = new SQLiteHelper(dbPath);
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Add new user with encrypted password
     * @return true if successful
     */
    public boolean addUser(String userId, String username, String password, String role) {
        try {
            String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_ROUNDS));
            int affected = dbHelper.executeUpdate(
                "INSERT INTO users (user_id, username, password_hash, role) VALUES (?, ?, ?, ?)",
                userId, username, passwordHash, role);
            return affected > 0;
        } catch (SQLException e) {
            System.err.println("Add user failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Find user by username
     * @return User data map or null if not found
     */
    public Map<String, Object> findUserByUsername(String username) {
        try {
            List<Map<String, Object>> users = dbHelper.executeQuery(
                "SELECT * FROM users WHERE username = ?", username);
            return users.isEmpty() ? null : users.get(0);
        } catch (SQLException e) {
            System.err.println("Find user failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Find user by ID
     * @return User data map or null if not found
     */
    public Map<String, Object> findUserById(String userId) {
        try {
            List<Map<String, Object>> users = dbHelper.executeQuery(
                "SELECT * FROM users WHERE user_id = ?", userId);
            return users.isEmpty() ? null : users.get(0);
        } catch (SQLException e) {
            System.err.println("Find user failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get all users
     * @return List of user maps
     */
    public List<Map<String, Object>> getAllUsers() {
        try {
            return dbHelper.executeQuery("SELECT user_id, username, role, frozen FROM users");
        } catch (SQLException e) {
            System.err.println("Get all users failed: " + e.getMessage());
            return List.of(); // Return empty list on error
        }
    }

    /**
     * Verify user credentials
     * @return true if password matches
     */
    public boolean verifyCredentials(String username, String password) {
        Map<String, Object> user = findUserByUsername(username);
        if (user == null) return false;
        
        String storedHash = (String) user.get("password_hash");
        return BCrypt.checkpw(password, storedHash);
    }

    /**
     * Update user information (excluding password)
     * @return true if successful
     */
    public boolean updateUser(String userId, String username, String role, boolean frozen) {
        try {
            int affected = dbHelper.executeUpdate(
                "UPDATE users SET username = ?, role = ?, frozen = ? WHERE user_id = ?",
                username, role, frozen, userId);
            return affected > 0;
        } catch (SQLException e) {
            System.err.println("Update user failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update user password
     * @return true if successful
     */
    public boolean updateUser(String userId, String newPassword) {
        try {
            String newHash = BCrypt.hashpw(newPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
            int affected = dbHelper.executeUpdate(
                "UPDATE users SET password_hash = ? WHERE user_id = ?",
                newHash, userId);
            return affected > 0;
        } catch (SQLException e) {
            System.err.println("Password update failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete user by ID
     * @return true if successful
     */
    public boolean deleteUser(String userId) {
        try {
            int affected = dbHelper.executeUpdate(
                "DELETE FROM users WHERE user_id = ?", userId);
            return affected > 0;
        } catch (SQLException e) {
            System.err.println("Delete user failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if username exists
     * @return true if username already exists
     */
    public boolean usernameExists(String username) {
        try {
            List<Map<String, Object>> users = dbHelper.executeQuery(
                "SELECT 1 FROM users WHERE username = ?", username);
            return !users.isEmpty();
        } catch (SQLException e) {
            System.err.println("Check username failed: " + e.getMessage());
            return false;
        }
    }
}