package org.JBR.Service;

import io.javalin.http.Context;
import org.JBR.DAO.BorrowRecordDAO;

public class BorrowRecordsController {
    public static void getAllRecords(Context ctx) {
        BorrowRecordDAO borrowRecordDAO = new BorrowRecordDAO("library.db");
        try {
            ctx.json(borrowRecordDAO.getAllBorrowRecords());
        } catch (Exception e) {
            ctx.status(500).json("Failed to retrieve borrow records: " + e.getMessage());
        } finally {
            borrowRecordDAO.close();
        }
    }
    public static void getRecord(Context ctx) {
        // Implement logic to retrieve a specific borrow record by ID
    }
    public static void createRecord(Context ctx) {
        BorrowRecordDAO borrowRecordDAO = new BorrowRecordDAO("library.db");
        try {
            // Extract data from the request
            String recordId = ctx.formParam("record_id");
            String userId = ctx.formParam("user_id");
            String bookId = ctx.formParam("book_id");
            String borrowDate = ctx.formParam("borrow_date");
            String dueDate = ctx.formParam("due_date");

            // Call the DAO method to create the record
            boolean success = borrowRecordDAO.addBorrowRecord(recordId, userId, bookId, borrowDate, dueDate);
            if (success) {
                ctx.status(201).json("Borrow record created successfully");
            } else {
                ctx.status(500).json("Failed to create borrow record");
            }
        } catch (Exception e) {
            ctx.status(500).json("Failed to create borrow record: " + e.getMessage());
        } finally {
            borrowRecordDAO.close();
        }
    }
    public static void updateRecord(Context ctx) {
        BorrowRecordDAO borrowRecordDAO = new BorrowRecordDAO("library.db");
        try {
            // Extract data from the request
            String recordId = ctx.formParam("record_id");
            String userId = ctx.formParam("user_id");
            String bookId = ctx.formParam("book_id");
            String borrowDate = ctx.formParam("borrow_date");
            String dueDate = ctx.formParam("due_date");

            // Call the DAO method to update the record
            boolean success = borrowRecordDAO.updateBorrowRecord(recordId, userId, bookId, borrowDate, dueDate);
            if (success) {
                ctx.status(200).json("Borrow record updated successfully");
            } else {
                ctx.status(500).json("Failed to update borrow record");
            }
        } catch (Exception e) {
            ctx.status(500).json("Failed to update borrow record: " + e.getMessage());
        } finally {
            borrowRecordDAO.close();
        }
    }
    public static void deleteRecord(Context ctx) {
        BorrowRecordDAO borrowRecordDAO = new BorrowRecordDAO("library.db");
        try {
            String recordId = ctx.formParam("record_id");
            boolean success = borrowRecordDAO.deleteBorrowRecord(recordId);
            if (success) {
                ctx.status(200).json("Borrow record deleted successfully");
            } else {
                ctx.status(500).json("Failed to delete borrow record");
            }
        } catch (Exception e) {
            ctx.status(500).json("Failed to delete borrow record: " + e.getMessage());
        } finally {
            borrowRecordDAO.close();
        }
    }
    public static void getUserBorrowRecords(Context ctx) {
        BorrowRecordDAO borrowRecordDAO = new BorrowRecordDAO("library.db");
        try {
            String userId = ctx.formParam("user_id");
            ctx.json(borrowRecordDAO.getBorrowRecordsByUser(userId));
        } catch (Exception e) {
            ctx.status(500).json("Failed to retrieve borrow records for user: " + e.getMessage());
        } finally {
            borrowRecordDAO.close();
        }
    }
    
}