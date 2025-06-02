package org.visier.coursedesign.Service;
import org.visier.coursedesign.ApiClient.ApiClient;
import org.json.JSONObject;

public class BorrowRecordService {
    private BorrowRecordService() {
        // Private constructor to prevent instantiation
    }

    public static JSONObject addBorrowRecord(String recordJson) throws Exception {
        String response = ApiClient.sendRequest("POST", "/borrow-records", recordJson);
        return new JSONObject(response);
    }

    public static JSONObject getAllBorrowRecords() throws Exception {
        String response = ApiClient.sendRequest("GET", "/borrow-records", "");
        return new JSONObject(response);
    }

    public static JSONObject getBorrowRecord(String recordId) throws Exception {
        String response = ApiClient.sendRequest("GET", "/borrow-records/" + recordId, "");
        return new JSONObject(response);
    }

    public static JSONObject updateBorrowRecord(String recordId, String updateJson) throws Exception {
        String response = ApiClient.sendRequest("PATCH", "/borrow-records/" + recordId, updateJson);
        return new JSONObject(response);
    }

    public static JSONObject deleteBorrowRecord(String recordId) throws Exception {
        String response = ApiClient.sendRequest("DELETE", "/borrow-records/" + recordId, "");
        return new JSONObject(response);
    }

    public static JSONObject returnBorrowRecord(String recordId) throws Exception {
        String response = ApiClient.sendRequest("POST", "/borrow-records/" + recordId + "/return", "");
        return new JSONObject(response);
    }

}
