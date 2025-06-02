package org.visier.coursedesign.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.geometry.Insets;

import org.json.JSONArray;
import org.json.JSONObject;
import org.visier.coursedesign.Entity.*;
import java.util.Optional;
import org.visier.coursedesign.Service.UserService;

public class UserManagementController {
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private Button addButton;
    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, String> userIdColumn;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, String> roleColumn;
    @FXML
    private TableColumn<User, Void> actionsColumn;
    @FXML
    private Label currentUserLabel;

    private ObservableList<User> userList = FXCollections.observableArrayList();
    private User currentUser;

    @FXML
    public void initialize() {
        // Initialize table columns
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Set up action buttons
        setupActionButtons();

        // Load sample data
        getAllUsers();

        // Set table data
        usersTable.setItems(userList);
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        currentUserLabel.setText("Logged in as: " + user.getUsername() + " (" + user.getRole() + ")");
        addButton.setDisable(!"ADMIN".equals(user.getRole()));
    }

    private void setupActionButtons() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final Button promoteButton = new Button("Promote");
            private final Button freezeButton = new Button("Freeze");
            private final HBox buttons = new HBox(5, editButton, deleteButton, promoteButton, freezeButton);

            {
                editButton.setOnAction(event -> handleEditUser(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(event -> handleDeleteUser(getTableView().getItems().get(getIndex())));
                promoteButton.setOnAction(event -> handlePromoteUser(getTableView().getItems().get(getIndex())));
                freezeButton.setOnAction(event -> handleFreezeUser(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());

                    if (user instanceof NormalUser) {
                        NormalUser normalUser = (NormalUser) user;
                        freezeButton.setText(normalUser.isFrozen() ? "Unfreeze" : "Freeze");
                    } else {
                        freezeButton.setVisible(false);
                    }

                    setGraphic(buttons);
                }
            }
        });
    }

    private void getAllUsers() {
        try {
            JSONObject response = UserService.getAllUsers();
            if (response.getBoolean("success")) {
                JSONArray usersArray = response.getJSONArray("users");
                userList.clear();
                for (int i = 0; i < usersArray.length(); i++) {
                    JSONObject userJson = usersArray.getJSONObject(i);
                    // 使用与后端一致的字段名
                    String userId = userJson.getString("user_id");
                    String username = userJson.getString("username");
                    String role = userJson.getString("role");
                    Boolean frozen = userJson.getInt("frozen") == 1; // 后端可能没有返回 frozen 字段

                    // 角色比较改为与后端一致的大小写
                    if ("Admin".equalsIgnoreCase(role)) {
                        userList.add(new Admin(userId, username, "", false)); // 密码留空
                    } else {
                        userList.add(new NormalUser(userId, username, "", frozen)); // 密码留空
                    }
                }
            } else {
                System.err.println("Failed to load users: " + response.optString("error", "Unknown error"));
            }
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
            e.printStackTrace(); // 打印完整堆栈便于调试
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().toLowerCase();
        usersTable.setItems(query.isEmpty() ? userList
                : userList.filtered(user -> user.getUsername().toLowerCase().contains(query) ||
                        user.getUserId().toLowerCase().contains(query) ||
                        user.getRole().toLowerCase().contains(query)));
    }

    @FXML
    private void handleAddUser() {
        showUserDialog(null);
    }

    private void handleEditUser(User user) {
        showUserDialog(user);
    }

    private void handleDeleteUser(User user) {
        showConfirmationDialog("Confirm Deletion", "Delete User",
                "Are you sure you want to delete user: " + user.getUsername() + "?",
                () -> userList.remove(user));
    }

    private void handlePromoteUser(User user) {
        showConfirmationDialog("Confirm Promotion", "Promote to Admin",
                "Promote " + user.getUsername() + " to administrator?",
                () -> {
                    int index = userList.indexOf(user);
                    userList.set(index, new Admin(user.getUserId(), user.getUsername(), user.getPasswordHash(), false));
                });
    }

    private void handleFreezeUser(User user) {
        if (user instanceof NormalUser) {
            NormalUser normalUser = (NormalUser) user;
            String action = normalUser.isFrozen() ? "Unfreeze" : "Freeze";

            showConfirmationDialog("Confirm " + action, action + " User",
                    action + " user " + user.getUsername() + "?",
                    () -> {
                        if (normalUser.isFrozen()) {
                            // Call admin's unfreeze method if current user is admin
                            if (currentUser instanceof Admin) {
                                ((Admin) currentUser).unfreezeUser(normalUser);
                            }
                            normalUser.setFrozen(false);
                        } else {
                            // Call admin's freeze method if current user is admin
                            if (currentUser instanceof Admin) {
                                ((Admin) currentUser).freezeUser(normalUser);
                            }
                            normalUser.setFrozen(true);
                        }
                        usersTable.refresh();
                    });
        }
    }

    private void showConfirmationDialog(String title, String header, String content, Runnable action) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                action.run();
            }
        });
    }

    private void showUserDialog(User user) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle(user == null ? "Add User" : "Edit User");

        // Set buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField userIdField = new TextField();
        userIdField.setPromptText("User ID");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("NORMAL", "ADMIN");

        if (user != null) {
            userIdField.setText(user.getUserId());
            usernameField.setText(user.getUsername());
            roleCombo.setValue(user.getRole());
            userIdField.setDisable(true);
            roleCombo.setDisable(!isCurrentUserAdmin());
        } else {
            roleCombo.setValue("NORMAL");
        }

        grid.add(new Label("User ID:"), 0, 0);
        grid.add(userIdField, 1, 0);
        grid.add(new Label("Username:"), 0, 1);
        grid.add(usernameField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);

        if (isCurrentUserAdmin()) {
            grid.add(new Label("Role:"), 0, 3);
            grid.add(roleCombo, 1, 3);
        }

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(buttonType -> createUserFromDialog(buttonType, user, userIdField, usernameField,
                passwordField, roleCombo));

        processDialogResult(dialog, user);
    }

    private boolean isCurrentUserAdmin() {
        return currentUser != null && "ADMIN".equals(currentUser.getRole());
    }

    private User createUserFromDialog(ButtonType buttonType, User user,
            TextField userIdField, TextField usernameField,
            PasswordField passwordField, ComboBox<String> roleCombo) {
        if (buttonType == ButtonType.OK) {
            String userId = userIdField.getText();
            String username = usernameField.getText();
            String password = passwordField.getText();
            String role = roleCombo.getValue();

            if (user == null) {
                return "ADMIN".equals(role)
                        ? new Admin(userId, username, password, false)
                        : new NormalUser(userId, username, password, false);
            } else {
                user.setUsername(username);
                if (!password.isEmpty()) {
                    user.setPasswordHash(password);
                }

                if (isCurrentUserAdmin()) {
                    return handleRoleChange(user, role);
                }
                return user;
            }
        }
        return null;
    }

    private User handleRoleChange(User user, String role) {
        if ("ADMIN".equals(role) && user instanceof NormalUser) {
            return new Admin(user.getUserId(), user.getUsername(), user.getPasswordHash(), user.isFrozen());
        } else if ("NORMAL".equals(role) && user instanceof Admin) {
            return new NormalUser(user.getUserId(), user.getUsername(), user.getPasswordHash(), user.isFrozen());
        }
        return user;
    }

    private void processDialogResult(Dialog<User> dialog, User user) {
        Optional<User> result = dialog.showAndWait();
        result.ifPresent(newUser -> {
            if (user == null) {
                userList.add(newUser);
            } else {
                int index = userList.indexOf(user);
                userList.set(index, newUser);
            }
        });
    }
}