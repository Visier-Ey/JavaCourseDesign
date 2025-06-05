package org.visier.coursedesign.Service;

import org.json.JSONObject;
import org.visier.coursedesign.ApiClient.ApiClient;

public final class UserService {
    private UserService() {}

    public static JSONObject login(String username, String password) throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("username", username);
        requestBody.put("password", password);

        String response = ApiClient.sendRequest("POST", "/users/login", requestBody.toString());
        return new JSONObject(response);
    }

    public static JSONObject getAllUsers() throws Exception {
        String response = ApiClient.sendRequest("GET", "/users", "");
        return new JSONObject(response);
    }

    public static JSONObject getUser(String userId) throws Exception {
        String response = ApiClient.sendRequest("GET", "/users/" + userId, "");
        return new JSONObject(response);
    }

    public static JSONObject createUser(String userJson) throws Exception {
        String response = ApiClient.sendRequest("POST", "/users", userJson);
        return new JSONObject(response);
    }

    public static JSONObject registerUser(String userJson) throws Exception {
        String response = ApiClient.sendRequest("POST", "/users/register", userJson);
        return new JSONObject(response);
    }

    public static JSONObject updateUser(String userId, String updateJson) throws Exception {
        String response = ApiClient.sendRequest("PATCH", "/users/" + userId, updateJson);
        return new JSONObject(response);
    }

    public static JSONObject deleteUser(String userId) throws Exception {
        String response = ApiClient.sendRequest("DELETE", "/users/" + userId, "");
        return new JSONObject(response);
    }

    public static JSONObject freezeUser(String userId) throws Exception {
        String response = ApiClient.sendRequest("PATCH", "/users/" + userId + "/freeze", "");
        return new JSONObject(response);
    }
    public static JSONObject unfreezeUser(String userId) throws Exception {
        String response = ApiClient.sendRequest("PATCH", "/users/" + userId + "/unfreeze", "");
        return new JSONObject(response);
    }
    public static JSONObject promoteUser(String userId) throws Exception {
        String response = ApiClient.sendRequest("PATCH", "/users/" + userId + "/promote", "");
        return new JSONObject(response);
    }
    public static JSONObject getUserRecommendations() throws Exception {
        String response = ApiClient.sendRequest("GET", "/users/recommend", "");
        return new JSONObject(response);
    }
}