package org.JBR.Service;

import io.javalin.http.Context;
import org.JBR.DAO.BookDAO;

import java.util.HashMap;
import java.util.Map;


public class BookController {
    public static void getAllBooks(Context ctx) {
        BookDAO bookDAO = new BookDAO("library.db");
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("books", bookDAO.getAllBooks());
            response.put("success", true);
            ctx.json(response);
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", "Failed to retrieve books: " + e.getMessage()));
        } finally {
            bookDAO.close();
        }
    }

    public static void getBook(Context ctx) {
        // BookDAO bookDAO = new BookDAO("library.db");
        // try {
        //     String bookId = ctx.pathParam("id");
        //     ctx.json(bookDAO.getBook(bookId));
        // } catch (Exception e) {
        //     ctx.status(500).json(Map.of("success", false, "message", "Failed to retrieve book: " + e.getMessage()));
        // } finally {
        //     bookDAO.close();
        // }
    }

    public static void createBook(Context ctx) {
       BookDAO bookDAO = new BookDAO("library.db");
        try {
            String bookId = ctx.formParam("bookId");
            String title = ctx.formParam("title");
            String author = ctx.formParam("author");
            String isbn = ctx.formParam("isbn");

            if (bookId == null || title == null || author == null || isbn == null) {
                ctx.status(400).json(Map.of("success", false, "message", "All fields are required"));
                return;
            }

            boolean success = bookDAO.addBook(bookId, title, author, isbn);
            if (success) {
                ctx.status(201).json(Map.of("success", true, "message", "Book created successfully"));
            } else {
                ctx.status(500).json(Map.of("success", false, "message", "Failed to create book"));
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", "Failed to create book: " + e.getMessage()));
        } finally {
            bookDAO.close();
        }
    }

    public static void updateBook(Context ctx) {
        BookDAO bookDAO = new BookDAO("library.db");
        try {
            String bookId = ctx.pathParam("id");
            if (bookId == null || bookId.isEmpty()) {
                ctx.status(400).json(Map.of("success", false, "message", "Book ID is required"));
                return;
            }

            String title = ctx.formParam("title");
            String author = ctx.formParam("author");
            String isbn = ctx.formParam("isbn");

            if (title == null || author == null || isbn == null) {
                ctx.status(400).json(Map.of("success", false, "message", "All fields are required"));
                return;
            }

            boolean success = bookDAO.updateBookAvailability(bookId, true); // Assuming availability is being updated
            if (success) {
                ctx.status(200).json(Map.of("success", true, "message", "Book updated successfully"));
            } else {
                ctx.status(404).json(Map.of("success", false, "message", "Book not found"));
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", "Failed to update book: " + e.getMessage()));
        } finally {
            bookDAO.close();
        }
    }

    public static void deleteBook(Context ctx) {
    //     BookDAO bookDAO = new BookDAO("library.db");
    //     try {
    //         String bookId = ctx.pathParam("id");
    //         if (bookId == null || bookId.isEmpty()) {
    //             ctx.status(400).json(Map.of("success", false, "message", "Book ID is required"));
    //             return;
    //         }

    //         boolean success = bookDAO.deleteBook(bookId);
    //         if (success) {
    //             ctx.status(200).json(Map.of("success", true, "message", "Book deleted successfully"));
    //         } else {
    //             ctx.status(404).json(Map.of("success", false, "message", "Book not found"));
    //         }
    //     } catch (Exception e) {
    //         ctx.status(500).json(Map.of("success", false, "message", "Failed to delete book: " + e.getMessage()));
    //     } finally {
    //         bookDAO.close();
    //     }
    }
}
