package org.visier.coursedesign.Entity;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.BooleanProperty;

public class Book {
    private final SimpleStringProperty bookId;
    private final StringProperty title;
    private final SimpleStringProperty author;
    private final SimpleStringProperty isbn;
    private final SimpleBooleanProperty available;

     public Book(String bookId, String title, String author, String isbn, boolean available) {
        this.bookId = new SimpleStringProperty(bookId);
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleStringProperty(author);
        this.isbn = new SimpleStringProperty(isbn);
        this.available = new SimpleBooleanProperty(available);
    }

    // Getters and setters for bookId
    public String getBookId() {
        return bookId.get();
    }

    public void setBookId(String bookId) {
        this.bookId.set(bookId);
    }

    public SimpleStringProperty bookIdProperty() {
        return bookId;
    }

    // Getters and setters for title
    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return title;
    }

    // Getters and setters for author
    public String getAuthor() {
        return author.get();
    }

    public void setAuthor(String author) {
        this.author.set(author);
    }

    public SimpleStringProperty authorProperty() {
        return author;
    }

    // Getters and setters for isbn
    public String getIsbn() {
        return isbn.get();
    }

    public void setIsbn(String isbn) {
        this.isbn.set(isbn);
    }

    public SimpleStringProperty isbnProperty() {
        return isbn;
    }

    // Getters and setters for available
    public boolean isAvailable() {
        return available.get();
    }

    public void setAvailable(boolean available) {
        this.available.set(available);
    }

    public BooleanProperty availableProperty() {
        return available;
    }
}