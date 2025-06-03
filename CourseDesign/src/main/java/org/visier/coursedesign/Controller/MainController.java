package org.visier.coursedesign.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;

import org.visier.coursedesign.Manager.SceneManager;
import org.visier.coursedesign.Session.UserSession;

import javafx.scene.Node;

import java.net.URL;

public class MainController {
    @FXML
    private Button logoutButton;
    @FXML
    private Button dashboardButton;
    @FXML
    private Button booksButton;
    @FXML
    private Button usersButton;
    @FXML
    private Button recordsButton;
    @FXML
    private StackPane contentPane;

    @FXML
    private void initialize() {
        //* register the button actions */
        logoutButton.setOnAction(e -> handleLogout());
        dashboardButton.setOnAction(e -> loadView("dashboard"));
        booksButton.setOnAction(e -> loadView("booksManagement"));
        usersButton.setOnAction(e -> loadView("userManagement"));
        recordsButton.setOnAction(e -> loadView("borrowRecords"));
    }

    public void setVisible(){
        usersButton.setVisible(UserSession.getCurrentUser().getRole().equals("ADMIN"));
    }

    private void handleLogout() {
        //* Switch to the login scene */
        SceneManager.switchTo("login");
    }

    private void loadView(String viewName) {
        try {
            //* Load the corresponding FXML file */
            String fxmlFile = "/org/visier/coursedesign/" + viewName + ".fxml";
            URL fxmlUrl = getClass().getResource(fxmlFile);
            if (fxmlUrl == null) {
                throw new RuntimeException("Cannot find FXML file for: " + viewName);
            }
            Node view = FXMLLoader.load(fxmlUrl);
            //* Clear existing content and add new content */
            contentPane.getChildren().clear();
            contentPane.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: Handle the error gracefully, maybe show an alert
        }
    }
}