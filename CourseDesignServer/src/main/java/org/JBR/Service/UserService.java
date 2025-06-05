package org.JBR.Service;

import org.JBR.DAO.UserDAO;
import org.JBR.Utils.JwtUtil;
import org.JBR.Utils.Utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.util.Util;
import io.javalin.websocket.WsConfig;
import java.util.HashMap;
import java.util.Map;
import static org.JBR.Utils.Utils.createErrorResponse;;

public class UserService {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String DB_PATH = "library.db"; // 数据库路径

    public static void getAllUsers(Context ctx) { 
        UserDAO userDAO = new UserDAO(DB_PATH);
        try {
            // 查询所有用户
            Map<String, Object> response = new HashMap<>();
            response.put("users", userDAO.getAllUsers());
            response.put("success", true);
            ctx.json(response);
        } catch (Exception e) {
            ctx.status(500).json(Map.of("success", false, "message", "Failed to retrieve users: " + e.getMessage()));
        } finally {
            userDAO.close();
        }
     }
    public static void createUser(Context ctx) {  }
    public static void getUser(Context ctx) {  }
    public static void updateUser(Context ctx) { 
        UserDAO userDAO = new UserDAO(DB_PATH);
        try {
            String userId = ctx.pathParam("id");
            if (userId == null || userId.isEmpty()) {
                ctx.status(400).json(createErrorResponse("User ID is required"));
                return;
            }

            JsonNode requestBody = objectMapper.readTree(ctx.body());
            String username = requestBody.path("username").asText();
            String role = requestBody.path("role").asText();
            boolean frozen = requestBody.path("frozen").asBoolean(false); // 默认值为false

            // 更新用户信息
            boolean success = userDAO.updateUser(userId, username, role, frozen);
            if (success) {
                ctx.status(200).json(Map.of("success", true, "message", "User updated successfully"));
            } else {
                ctx.status(404).json(createErrorResponse("User not found"));
            }
        } catch (Exception e) {
            ctx.status(500).json(createErrorResponse("Failed to update user: " + e.getMessage()));
        } finally {
            userDAO.close();
        }
     }
    public static void deleteUser(Context ctx) { 
        UserDAO userDAO = new UserDAO(DB_PATH);
        try {
            String userId = ctx.pathParam("id");
            if (userId == null || userId.isEmpty()) {
                ctx.status(400).json(createErrorResponse("User ID is required"));
                return;
            }

            boolean success = userDAO.deleteUser(userId);
            if (success) {
                ctx.status(200).json(Map.of("success", true, "message", "User deleted successfully"));
            } else {
                ctx.status(404).json(createErrorResponse("User not found"));
            }
        } catch (Exception e) {
            ctx.status(500).json(createErrorResponse("Failed to delete user: " + e.getMessage()));
        } finally {
            userDAO.close();
        }
     }
    public static void webSocketEvents(WsConfig wsc) {  }

    public static void login(Context ctx) {
        UserDAO userDAO = new UserDAO(DB_PATH);
        try {
            // 解析JSON请求体
            JsonNode requestBody = objectMapper.readTree(ctx.body());
            String username = requestBody.path("username").asText();
            String password = requestBody.path("password").asText();

            // 验证输入
            if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
                ctx.status(400).json(createErrorResponse("Username and password are required"));
                return;
            }

            // 从数据库查询用户
            Map<String, Object> user = userDAO.findUserByUsername(username);
            if (user == null) {
                ctx.status(401).json(createErrorResponse("Invalid username or password"));
                return;
            }

            // 验证密码 (实际项目中应该使用密码哈希验证)
            if (!userDAO.verifyCredentials(username, password)) {
                ctx.status(401).json(createErrorResponse("Invalid username or password"));
                return;
                
            }

            // 生成JWT令牌
            Map<String, Object> response = new HashMap<>();
            String token = JwtUtil.generateToken(user.get("user_id").toString(), user.get("role").toString());
            response.put("success", true);
            response.put("token", token);
            response.put("role", user.get("role")); 
            response.put("user_id", user.get("user_id")); // 返回用户ID
            response.put("message", "Login successful");

            ctx.json(response);
            
        } catch (Exception e) {
            ctx.status(400).json(createErrorResponse("Invalid request: " + e.getMessage()));
        } finally {
            userDAO.close();
        }
    }

    public static void register(Context ctx) {
        UserDAO userDAO = new UserDAO(DB_PATH);
        try {
            JsonNode requestBody = objectMapper.readTree(ctx.body());
            String username = requestBody.path("username").asText();
            String password = requestBody.path("password").asText();
            String role = "NORMAL";

            if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
                ctx.status(400).json(createErrorResponse("Username and password are required"));
                return;
            }

            // 检查用户名是否已存在
            if (userDAO.findUserByUsername(username) != null) {
                ctx.status(400).json(createErrorResponse("Username already exists"));
                return;
            }

            // 生成用户ID (实际项目中可以用UUID)
            String userId = "U" + System.currentTimeMillis();
            
            // 添加用户到数据库 (注意: 实际项目应该存储密码哈希而非明文)
            boolean success = userDAO.addUser(userId, username, password, role);
            if (success) {
                ctx.status(201).json(Map.of(
                    "success", true,
                    "message", "User registered successfully"
                ));
            } else {
                ctx.status(500).json(createErrorResponse("Failed to register user"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(400).json(createErrorResponse("Invalid request: " + e.getMessage()));
        } finally {
            userDAO.close();
        }
    }

    public static void freezeUser(Context ctx) {
        UserDAO userDAO = new UserDAO(DB_PATH);
        try {
            String userId = ctx.pathParam("id");
            if (userId == null || userId.isEmpty()) {
                ctx.status(400).json(createErrorResponse("User ID is required"));
                return;
            }

            boolean success = userDAO.freezeUser(userId);
            if (success) {
                ctx.status(200).json(Map.of("success", true, "message", "User frozen successfully"));
            } else {
                ctx.status(404).json(createErrorResponse("User not found"));
            }
        } catch (Exception e) {
            ctx.status(500).json(createErrorResponse("Failed to freeze user: " + e.getMessage()));
        } finally {
            userDAO.close();
        }
    }

    public static void unfreezeUser(Context ctx) {
        UserDAO userDAO = new UserDAO(DB_PATH);
        try {
            String userId = ctx.pathParam("id");
            if (userId == null || userId.isEmpty()) {
                ctx.status(400).json(createErrorResponse("User ID is required"));
                return;
            }

            boolean success = userDAO.unfreezeUser(userId);
            if (success) {
                ctx.status(200).json(Map.of("success", true, "message", "User unfrozen successfully"));
            } else {
                ctx.status(404).json(createErrorResponse("User not found"));
            }
        } catch (Exception e) {
            ctx.status(500).json(createErrorResponse("Failed to unfreeze user: " + e.getMessage()));
        } finally {
            userDAO.close();
        }
    }

    public static void promoteUser(Context ctx) {
        UserDAO userDAO = new UserDAO(DB_PATH);
        try {
            String userId = ctx.pathParam("id");
            if (userId == null || userId.isEmpty()) {
                ctx.status(400).json(createErrorResponse("User ID is required"));
                return;
            }

            boolean success = userDAO.promoteUser(userId);
            if (success) {
                ctx.status(200).json(Map.of("success", true, "message", "User promoted successfully"));
            } else {
                ctx.status(404).json(createErrorResponse("User not found"));
            }
        } catch (Exception e) {
            ctx.status(500).json(createErrorResponse("Failed to promote user: " + e.getMessage()));
        } finally {
            userDAO.close();
        }
    }

    public static void getUserRecommendations(Context ctx) {
        UserDAO userDAO = new UserDAO(DB_PATH);
        try {
            String userId = JwtUtil.extractUserInfo(ctx.req().getHeader("Authorization")).get("user_id");
            Map<String, Object> recommendations = userDAO.getUserRecommendations(userId);
            if (recommendations != null) {
                ctx.status(200).json(Map.of("success", true, "recommendations", recommendations));
            } else {
                ctx.status(404).json(createErrorResponse("No recommendations found for this user"));
            }
        } catch (Exception e) {
            ctx.status(500).json(createErrorResponse("Failed to get recommendations: " + e.getMessage()));
            e.printStackTrace();
        } finally {
            userDAO.close();
        }
    }

}