package org.visier.coursedesign.Entity;

import java.util.Date;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class BorrowRecord {
    private String recordId;
    private Date borrowDate;
    private Date dueDate;
    private User user;
    private Book book;

    public BorrowRecord(User user, Book book) {
        this.recordId = generateRecordId();
        this.borrowDate = new Date();
        this.dueDate = calculateDueDate();
        this.user = user;
        this.book = book;
        book.setAvailable(false);
    }

    private String generateRecordId() {
        return "REC-" + System.currentTimeMillis();
    }

    private Date calculateDueDate() {
        long twoWeeks = 14 * 24 * 60 * 60 * 1000; // 2 weeks in milliseconds
        return new Date(System.currentTimeMillis() + twoWeeks);
    }

    public void generateOverdueNotice() {
        if (new Date().after(dueDate)) {
            System.out.println("Overdue notice for: " + user.getUsername() +
                    " regarding book: " + book.getTitle());
        }
    }

    public SimpleStringProperty recordIdProperty() {
        return new SimpleStringProperty(recordId);
    }

    public SimpleObjectProperty<Date> borrowDateProperty() {
        return new SimpleObjectProperty<>(borrowDate);
    }

    public SimpleObjectProperty<Date> dueDateProperty() {
        return new SimpleObjectProperty<>(dueDate);
    }


    // Getters
    public String getRecordId() {
        return recordId;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public User getUser() {
        return user;
    }

    public Book getBook() {
        return book;
    }
}