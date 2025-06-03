package org.visier.coursedesign.Manager;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class SceneManager {
    private static Stage stage;
    private static final Map<String, Scene> scenes = new ConcurrentHashMap<>();

    // * Initialize the SceneManager */
    public static void initialize(Stage primaryStage) {
        if (primaryStage == null) {
            throw new IllegalArgumentException("Primary stage cannot be null");
        }
        stage = primaryStage;
    }

    // * Add a scene to the SceneManager */
    public static void addScene(String name, Scene scene, Object controller) {
        if (name == null || scene == null) {
            throw new IllegalArgumentException("Name and scene cannot be null");
        }
        scene.getProperties().put("controller", controller);
        scenes.put(name, scene);
    }

    // * Remove a scene from the SceneManager */
    public static void removeScene(String name) {
        scenes.remove(name);
    }

    // * Switch to a scene by name, optionally configuring the controller */
    public static void switchTo(String sceneName) {
        switchTo(sceneName, null);
    }

    // * Switch to a scene by name, allowing for controller configuration */
    public static void switchTo(String sceneName, Consumer<Object> controllerConfigurator) {

        if (!scenes.containsKey(sceneName)) {
            throw new IllegalArgumentException("Scene not found: " + sceneName);
        }

        Scene scene = scenes.get(sceneName);
        Object controller = scene.getProperties().get("controller");

        if (controllerConfigurator != null && controller != null) {
            controllerConfigurator.accept(controller);
        }

        stage.setScene(scene);
    }

    // * Get the current scene */
    public static Scene getCurrentScene() {
        return stage.getScene();
    }

    // * Get the primary stage */
    public static Stage getStage() {
        return stage;
    }
}