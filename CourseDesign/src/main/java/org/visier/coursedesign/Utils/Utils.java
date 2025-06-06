package org.visier.coursedesign.Utils;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.util.Duration;
import javafx.scene.text.Text;

public class Utils {
    // 通知类型枚举
    public enum NotificationType {
        INFO, SUCCESS, ERROR
    }

    // 显示错误通知(非阻塞,右上角)
    public static void showErrorAlert(String header, String content) {
        showNotification("Error", header + "\n" + content, NotificationType.ERROR);
    }

    // 显示成功通知(非阻塞,右上角)
    public static void showSuccessAlert(String header, String content) {
        showNotification("Success", header + "\n" + content, NotificationType.SUCCESS);
    }

    // 显示普通通知(非阻塞,右上角)
    public static void Notification(String header, String content) {
        showNotification("Notification", header + "\n" + content, NotificationType.INFO);
    }

    // 核心通知显示方法
    private static void showNotification(String title, String message, NotificationType type) {
        // 基本参数设置
        final int WIDTH = 300;
        final int BASE_HEIGHT = 80;
        final int DURATION = 3000; // 显示时长(毫秒)

        // 使用Text控件代替Label以获得更好的文本布局控制
        Text titleText = new Text(title);
        titleText.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Text messageText = new Text(message);
        messageText.setStyle("-fx-font-size: 13px;");
        messageText.setWrappingWidth(WIDTH - 20); // 设置换行宽度

        VBox root = new VBox(5, titleText, messageText);
        root.setStyle(getNotificationStyle(type));
        root.setAlignment(Pos.TOP_LEFT);
        
        // 计算动态高度
        root.layout(); // 强制布局计算
        double textHeight = titleText.getLayoutBounds().getHeight() + 
                          messageText.getLayoutBounds().getHeight() + 20;
        double calculatedHeight = Math.max(BASE_HEIGHT, textHeight);
        
        root.setPrefSize(WIDTH, calculatedHeight);
        
        // 创建通知窗口
        Stage notificationStage = new Stage();
        notificationStage.initStyle(StageStyle.TRANSPARENT);
        notificationStage.setAlwaysOnTop(true);
        
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        notificationStage.setScene(scene);
        
        // 定位到主窗口右上角
        positionNotification(notificationStage, WIDTH);
        
        // 显示动画
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setOnFinished(e -> {
            // 显示3秒后开始淡出
            PauseTransition delay = new PauseTransition(Duration.millis(DURATION));
            delay.setOnFinished(event -> {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), root);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(evt -> notificationStage.close());
                fadeOut.play();
            });
            delay.play();
        });
        
        notificationStage.show();
        fadeIn.play();
    }

    // 定位通知窗口到主窗口右上角
    private static void positionNotification(Stage notificationStage, int width) {
        Stage primaryStage = (Stage) Stage.getWindows().stream()
                .filter(Window::isShowing)
                .findFirst()
                .orElse(null);
        
        if (primaryStage != null) {
            notificationStage.setX(primaryStage.getX() + primaryStage.getWidth() - width - 10);
            notificationStage.setY(primaryStage.getY() + 10);
        } else {
            notificationStage.setX(Screen.getPrimary().getVisualBounds().getWidth() - width - 10);
            notificationStage.setY(10);
        }
    }

    // 根据通知类型获取样式
    private static String getNotificationStyle(NotificationType type) {
        String baseStyle = "-fx-padding: 10; -fx-background-radius: 5; -fx-border-radius: 5; " +
                          "-fx-border-width: 1; -fx-background-insets: 0; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 1);";
        
        switch (type) {
            case ERROR:
                return baseStyle + "-fx-background-color: #ffebee; -fx-border-color: #ef9a9a; -fx-text-fill: #c62828;";
            case SUCCESS:
                return baseStyle + "-fx-background-color: #e8f5e9; -fx-border-color: #a5d6a7; -fx-text-fill: #2e7d32;";
            case INFO:
            default:
                return baseStyle + "-fx-background-color: #e3f2fd; -fx-border-color: #90caf9; -fx-text-fill: #1565c0;";
        }
    }
}