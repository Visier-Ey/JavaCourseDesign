package org.visier.coursedesign.Entity;

public class Admin extends User {
    public Admin() {
        super();
        setRole("ADMIN");
    }

    public Admin(String userId, String username) {
        super(userId, username);
        setRole("ADMIN");
    }

    public Admin(String userId, String username, String passwordHash,Boolean frozen) {
        super(userId, username, passwordHash, "ADMIN", frozen);
    }

    public Book addBook(String bookId, String title, String author, String isbn) {
        System.out.println("Admin adding new book: " + title);
        return new Book(bookId, title, author, isbn, true);
    }

    public boolean updateBook(Book book, String title, String author) {
        System.out.println("Admin updating book: " + book.getTitle());
        book.setTitle(title);
        book.setAuthor(author);
        return true;
    }

    public boolean deleteBook(Book book) {
        System.out.println("Admin deleting book: " + book.getTitle());
        return true;
    }

    public void freezeUser(NormalUser user) {
        System.out.println("Admin freezing user: " + user.getUsername());
    }

    public void unfreezeUser(NormalUser user) {
        System.out.println("Admin unfreezing user: " + user.getUsername());
    }

    public boolean promoteUser(NormalUser user) {
        System.out.println("Admin promoting user: " + user.getUsername());
        // In a real system, you would convert the NormalUser to Admin
        return true;
    }
}