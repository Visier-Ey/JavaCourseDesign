package org.visier.coursedesign.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.json.JSONObject;
import org.visier.coursedesign.ApiClient.ApiClient;
import org.visier.coursedesign.Manager.SceneManager;
import org.visier.coursedesign.Service.UserService;

public class LoginController {
    // Login components
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;
    
    // Registration components
    @FXML private TextField regUsernameField;
    @FXML private PasswordField regPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button registerButton;
    @FXML private Label regErrorLabel;

    @FXML
    private void initialize() {
        setupLoginHandlers();
        setupRegistrationHandlers();
    }

    private void setupLoginHandlers() {
        loginButton.setOnAction(event -> handleLogin());
        passwordField.setOnAction(event -> handleLogin());
    }

    private void setupRegistrationHandlers() {
        registerButton.setOnAction(event -> handleRegistration());
        confirmPasswordField.setOnAction(event -> handleRegistration());
    }

    private void handleLogin() {
        clearErrors();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showLoginError("Username and password are required");
            return;
        }

        try {
            JSONObject response = UserService.login(username, password);
            if (response.getBoolean("success")) {
                ApiClient.setToken(response.getString("token"));
                SceneManager.switchTo("main");
            } else {
                showLoginError(response.getString("error"));
            }
        } catch (Exception e) {
            showLoginError("Login failed. Please try again.");
            System.err.println("Login error: " + e.getMessage());
        }
    }

    private void handleRegistration() {
        clearErrors();
        String username = regUsernameField.getText().trim();
        String password = regPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showRegistrationError("All fields are required");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showRegistrationError("Passwords do not match");
            return;
        }

        try {
            // Using existing createUser method with JSON construction
            JSONObject userJson = new JSONObject();
            userJson.put("username", username);
            userJson.put("password", password); // Note: API should hash this
            userJson.put("role", "USER"); // Default role

            JSONObject response = UserService.registerUser(userJson.toString());
            if (response.getBoolean("success")) {
                handleSuccessfulRegistration();
            } else {
                showRegistrationError(response.getString("error"));
            }
        } catch (Exception e) {
            showRegistrationError("Registration failed. Please try again.");
            System.err.println("Registration error: " + e.getMessage());
        }
    }

    private void handleSuccessfulRegistration() {
        regUsernameField.clear();
        regPasswordField.clear();
        confirmPasswordField.clear();
        showRegistrationSuccess("Registration successful! Please login.");
    }

    private void clearErrors() {
        errorLabel.setVisible(false);
        regErrorLabel.setVisible(false);
    }

    private void showLoginError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void showRegistrationError(String message) {
        regErrorLabel.setStyle("-fx-text-fill: red;");
        regErrorLabel.setText(message);
        regErrorLabel.setVisible(true);
    }

    private void showRegistrationSuccess(String message) {
        regErrorLabel.setStyle("-fx-text-fill: green;");
        regErrorLabel.setText(message);
        regErrorLabel.setVisible(true);
    }
}