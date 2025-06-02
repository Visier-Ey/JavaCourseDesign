package org.visier.coursedesign.Entity;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class NormalUser extends User {
    public NormalUser() {
        super();
        setRole("NORMAL");
    }

    public NormalUser(String userId, String username) {
        super(userId, username);
        setRole("NORMAL");
    }

    public NormalUser(String userId, String username, String passwordHash, Boolean frozen) {
        super(userId, username, passwordHash, "NORMAL", frozen);
    }

    public void searchBooks(String query) {
        System.out.println("Normal user searching books: " + query);
    }

    public BorrowRecord borrowBook(Book book) {
        if (book.isAvailable()) {
            System.out.println("Borrowing book: " + book.getTitle());
            return new BorrowRecord(this, book);
        }
        return null;
    }

    public boolean returnBook(BorrowRecord record) {
        System.out.println("Returning book: " + record.getBook().getTitle());
        record.getBook().setAvailable(true);
        return true;
    }

  
}