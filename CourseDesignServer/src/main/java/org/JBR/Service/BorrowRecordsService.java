package org.JBR.Service;

import io.javalin.http.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.JBR.DAO.BorrowRecordDAO;

import com.fasterxml.jackson.databind.JsonNode;

import static org.JBR.Utils.Utils.createErrorResponse;
import org.JBR.Utils.JwtUtil;

public class BorrowRecordsService {

    public static void getAllRecords(Context ctx) {
        BorrowRecordDAO borrowRecordDAO = new BorrowRecordDAO("library.db");
        String userId = JwtUtil.extractUserInfo(ctx.req().getHeader("Authorization")).get("user_id");
        String role = JwtUtil.extractUserInfo(ctx.req().getHeader("Authorization")).get("role");
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("book-records", role.equals("ADMIN") ? borrowRecordDAO.getAllBorrowRecords() : borrowRecordDAO.getBorrowRecordsByUser(userId));
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
            JsonNode requestBody = ctx.bodyAsClass(JsonNode.class);
            String recordId = requestBody.path("record_id").asText();
            String userId = requestBody.path("user_id").asText();
            String bookId = requestBody.path("book_id").asText();
            String borrowDate = requestBody.path("borrow_date").asText();
            String dueDate = requestBody.path("due_date").asText();

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
            String recordId = ctx.pathParam("id");
            boolean success = borrowRecordDAO.deleteBorrowRecord(recordId);
            if (success) {
                System.out.println("Borrow record deleted successfully ID:"+ recordId);
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

    public static void returnBook(Context ctx) {
        BorrowRecordDAO borrowRecordDAO = new BorrowRecordDAO("library.db");
        try {
            JsonNode requestBody = ctx.bodyAsClass(JsonNode.class);
            String recordId = requestBody.path("record_id").asText();
            boolean success = borrowRecordDAO.returnBook(recordId);
            if (success) {
                ctx.status(200).json(Map.of("success", true, "message", "Book returned successfully"));
            } else {
                ctx.status(500).json(createErrorResponse("Failed to return book"));
            }
        } catch (Exception e) {
            ctx.status(500).json(createErrorResponse("Failed to return book: " + e.getMessage()));
        } finally {
            borrowRecordDAO.close();
        }
    }

    public static void getStatistics(Context ctx) {
        BorrowRecordDAO borrowRecordDAO = new BorrowRecordDAO("library.db");
        try {
            List<Map<String, Object>> statistics = borrowRecordDAO.getBorrowStatistics();
            ctx.json(Map.of("success", true, "statistics", statistics,"message", "Borrow statistics retrieved successfully"));
        } catch (Exception e) {
            ctx.status(500).json(createErrorResponse("Failed to retrieve statistics: " + e.getMessage()));
        } finally {
            borrowRecordDAO.close();
        }
    }
    
}