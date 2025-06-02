package org.visier.coursedesign;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.visier.coursedesign.Manager.SceneManager;


public class ApplicationMain extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneManager.initialize(primaryStage);

        //* Load the Login and Main FXML files and set up the scenes */
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        Scene loginScene = new Scene(loginLoader.load(), 1400, 800);
        SceneManager.addScene("login", loginScene);

        //* Load the Main FXML file and set up the scene */
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("main.fxml"));
        Scene mainScene = new Scene(mainLoader.load(), 1400, 800);
        SceneManager.addScene("main", mainScene);

        //* Set the initial scene to the login scene */
        SceneManager.switchTo("login");
        primaryStage.setTitle("Library Management System");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}