package org.visier.coursedesign.Session;

import org.visier.coursedesign.Entity.User;

public class UserSession {
    private static UserSession instance;
    private static User currentUser;

    private UserSession() {
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    // getter/setter
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void cleanSession() {
        currentUser = null;
    }
}
