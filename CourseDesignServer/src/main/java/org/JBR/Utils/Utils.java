package org.JBR.Utils;

import java.util.HashMap;
import java.util.Map;



public class Utils {
    public static Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        errorResponse.put("success", false);
        return errorResponse;
    }

    // public static String createSuccessResponse(String message, JSONPObject data) {
    //     if (data != null) {
    //         return "{\"success\": true, \"message\": \"" + message + "\", \"data\": " + data.toString() + "}";
    //     }
    //     return "{\"success\": true, \"message\": \"" + message + "\"}";
    // }
}
