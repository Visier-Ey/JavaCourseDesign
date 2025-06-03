package org.visier.coursedesign.Entity;

import java.util.Date;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BorrowRecord {
    private final StringProperty recordId;
    private final ObjectProperty<Date> borrowDate;
    private final ObjectProperty<Date> dueDate;
    private final ObjectProperty<User> user;
    private final ObjectProperty<Book> book;
    private final StringProperty status;

    public BorrowRecord(User user, Book book) {
        this.recordId = new SimpleStringProperty(generateRecordId());
        this.borrowDate = new SimpleObjectProperty<>(new Date());
        this.dueDate = new SimpleObjectProperty<>(calculateDueDate());
        this.user = new SimpleObjectProperty<>(user);
        this.book = new SimpleObjectProperty<>(book);
        this.status = new SimpleStringProperty("Borrowed");
        book.setAvailable(false);
    }

    public BorrowRecord(User user, Book book, String recordId, String status) {
        this.recordId = new SimpleStringProperty(recordId);
        this.borrowDate = new SimpleObjectProperty<>(new Date());
        this.dueDate = new SimpleObjectProperty<>(calculateDueDate());
        this.user = new SimpleObjectProperty<>(user);
        this.book = new SimpleObjectProperty<>(book);
        this.status = new SimpleStringProperty(status);
        book.setAvailable(false);
    }

    private String generateRecordId() {
        return "U" + System.currentTimeMillis();
    }

    private Date calculateDueDate() {
        long twoWeeks = 14 * 24 * 60 * 60 * 1000; // 2 weeks in milliseconds
        return new Date(System.currentTimeMillis() + twoWeeks);
    }

    public void generateOverdueNotice() {
        if (new Date().after(dueDate.get())) {
            System.out.println("Overdue notice for: " + user.get().getUsername() +
                    " regarding book: " + book.get().getTitle());
        }
    }

    // Property getters
    public StringProperty recordIdProperty() {
        return recordId;
    }

    public ObjectProperty<Date> borrowDateProperty() {
        return borrowDate;
    }

    public ObjectProperty<Date> dueDateProperty() {
        return dueDate;
    }

    public ObjectProperty<User> userProperty() {
        return user;
    }

    public ObjectProperty<Book> bookProperty() {
        return book;
    }

    public StringProperty statusProperty() {
        return status;
    }

    // Regular getters
    public String getRecordId() {
        return recordId.get();
    }

    public Date getBorrowDate() {
        return borrowDate.get();
    }

    public Date getDueDate() {
        return dueDate.get();
    }

    public User getUser() {
        return user.get();
    }

    public Book getBook() {
        return book.get();
    }

    public String getStatus() {
        return status.get();
    }

    // Setters
    public void setRecordId(String recordId) {
        this.recordId.set(recordId);
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate.set(borrowDate);
    }

    public void setDueDate(Date dueDate) {
        this.dueDate.set(dueDate);
    }

    public void setUser(User user) {
        this.user.set(user);
    }

    public void setBook(Book book) {
        this.book.set(book);
    }

    public void setStatus(String status) {
        this.status.set(status);
    }
}