package org.visier.coursedesign.Entity;

import javafx.beans.property.*;

public class User {
    private final StringProperty userId = new SimpleStringProperty();
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty passwordHash = new SimpleStringProperty();
    private final StringProperty role = new SimpleStringProperty();
    private final BooleanProperty frozen = new SimpleBooleanProperty(false);

    public User() {
        // Default constructor
    }

    public User(String userId, String username) {
        setUserId(userId);
        setUsername(username);
    }

    public User(String userId, String username, String passwordHash, String role, boolean frozen) {
        setUserId(userId);
        setUsername(username);
        setPasswordHash(passwordHash);
        setRole(role);
        setFrozen(frozen);
    }

    // Registration and login methods
    public boolean register(String username, String password) {
        setUsername(username);
        setPasswordHash(hashPassword(password));
        return true;
    }

    public boolean login(String username, String password) {
        return getUsername().equals(username) && verifyPassword(password);
    }

    private String hashPassword(String password) {
        // In production, use proper hashing like BCrypt
        return Integer.toString(password.hashCode());
    }

    private boolean verifyPassword(String password) {
        return getPasswordHash().equals(hashPassword(password));
    }

    // Property accessors
    public StringProperty userIdProperty() {
        return userId;
    }

    public String getUserId() {
        return userId.get();
    }

    public void setUserId(String userId) {
        this.userId.set(userId);
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public String getUsername() {
        return username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public StringProperty passwordHashProperty() {
        return passwordHash;
    }

    public String getPasswordHash() {
        return passwordHash.get();
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash.set(passwordHash);
    }

    public StringProperty roleProperty() {
        return role;
    }

    public String getRole() {
        return role.get();
    }

    public void setRole(String role) {
        this.role.set(role);
    }

      public boolean isFrozen() {
        return frozen.get();
    }

    public void setFrozen(boolean frozen) {
        this.frozen.set(frozen);
    }

    public BooleanProperty frozenProperty() {
        return frozen;
    }

    @Override
    public String toString() {
        return getUsername() + " (" + getRole() + ")";
    }
}