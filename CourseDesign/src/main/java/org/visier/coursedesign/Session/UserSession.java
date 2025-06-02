package org.visier.coursedesign.Session;

import org.visier.coursedesign.Entity.User;

public class UserSession {
    private static UserSession instance;
    private User currentUser;

    private UserSession() {
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    // getter/setter
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void cleanSession() {
        currentUser = null;
    }
}
