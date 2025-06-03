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
        // ? get the data and revise the status of the record,then return the record
        JSONObject record = getBorrowRecord(recordId);
        if (record == null) {
            throw new Exception("Borrow record not found");
        }
        record.put("status", "returned");
        String response = ApiClient.sendRequest("PATCH", "/borrow-records/" + recordId, record.toString());
        return new JSONObject(response);
    }

}
