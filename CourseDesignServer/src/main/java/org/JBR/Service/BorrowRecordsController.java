package org.JBR.Service;

import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

import org.JBR.DAO.BorrowRecordDAO;

import com.fasterxml.jackson.databind.JsonNode;

import static org.JBR.Utils.Utils.createErrorResponse;;

public class BorrowRecordsController {
    public static void getAllRecords(Context ctx) {
        BorrowRecordDAO borrowRecordDAO = new BorrowRecordDAO("library.db");
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("book-records", borrowRecordDAO.getAllBorrowRecords());
            response.put("success", true);
            response.put("message", "Borrow records retrieved successfully");
            ctx.json(response);
        } catch (Exception e) {
            ctx.status(500).json(createErrorResponse("Failed to retrieve borrow records: " + e.getMessage()));
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
            JsonNode requestBody = ctx.bodyAsClass(JsonNode.class);
            String userId = requestBody.path("user_id").asText();
            String bookId = requestBody.path("book_id").asText();
            String borrowDate = requestBody.path("borrow_date").asText();
            // Call the DAO method to create the record
            boolean success = borrowRecordDAO.addBorrowRecord(userId, bookId, borrowDate);
            if (success) {
                ctx.status(201).json(Map.of("success", true, "message", "Borrow record created successfully"));
            } else {
                ctx.status(500).json(createErrorResponse("Failed to create borrow record"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json(createErrorResponse("Failed to create borrow record: " + e.getMessage()));
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
                ctx.status(200).json(Map.of("success", true, "message", "Borrow record updated successfully"));
            } else {
                ctx.status(500).json(createErrorResponse("Failed to update borrow record"));
            }
        } catch (Exception e) {
            ctx.status(500).json(createErrorResponse("Failed to update borrow record: " + e.getMessage()));
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
                ctx.status(200).json(Map.of("success", true, "message", "Borrow record deleted successfully"));
            } else {
                ctx.status(500).json(createErrorResponse("Failed to delete borrow record"));
            }
        } catch (Exception e) {
            ctx.status(500).json(createErrorResponse("Failed to delete borrow record: " + e.getMessage()));
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
            ctx.status(500).json(createErrorResponse("Failed to retrieve borrow records for user: " + e.getMessage()));
        } finally {
            borrowRecordDAO.close();
        }
    }

    
    
}