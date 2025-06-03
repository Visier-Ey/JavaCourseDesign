package org.visier.coursedesign.Service;

import java.text.SimpleDateFormat;

import org.json.JSONObject;
import org.visier.coursedesign.ApiClient.ApiClient;
import java.util.Date;

public class BookService {
    private BookService() {
        // Private constructor to prevent instantiation
    }

    public static JSONObject getAllBooks() throws Exception {
        String response = ApiClient.sendRequest("GET", "/books", "");
        return new JSONObject(response);
    }

    public static JSONObject getBook(String bookId) throws Exception {
        String response = ApiClient.sendRequest("GET", "/books/" + bookId, "");
        return new JSONObject(response);
    }

    public static JSONObject createBook(String bookJson) throws Exception {
        String response = ApiClient.sendRequest("POST", "/books", bookJson);
        return new JSONObject(response);
    }

    public static JSONObject updateBook(String bookId, String updateJson) throws Exception {
        String response = ApiClient.sendRequest("PATCH", "/books/" + bookId, updateJson);
        return new JSONObject(response);
    }

    public static JSONObject deleteBook(String bookId) throws Exception {
        String response = ApiClient.sendRequest("DELETE", "/books/" + bookId, "");
        return new JSONObject(response);
    }

    public static JSONObject borrowBook(String bookId, String userId) throws Exception {
        String borrowDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        JSONObject res = BorrowRecordService.addBorrowRecord(
                new JSONObject()
                        .put("user_id", userId)
                        .put("book_id", bookId)
                        .put("borrow_date", borrowDate)
                        .toString());

        return res;
    }
}
