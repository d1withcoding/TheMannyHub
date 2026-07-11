package com.example.themannyhub;

import com.example.themannyhub.controllers.LoginController;
import com.example.themannyhub.services.AuthService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Launcher extends Application {

    private AuthService authService;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Show login dialog first
        if (!showLoginDialog()) {
            System.exit(0);
            return;
        }

        // Load the main window after successful login
        FXMLLoader loader = new FXMLLoader(
                Launcher.class.getResource("/com/example/themannyhub/MainWindow.fxml")
        );

        Scene scene = new Scene(loader.load(), 1100, 600);

        String userName = authService != null && authService.getCurrentUser() != null
                ? " - " + authService.getCurrentUser().getDisplayName()
                : "";
        primaryStage.setTitle("The Manny Hub" + userName);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(400);

        primaryStage.setOnCloseRequest(event -> {
            if (authService != null) {
                authService.logout();
            }
            System.exit(0);
        });

        primaryStage.show();
    }

    private boolean showLoginDialog() {
        try {
            // Use absolute path for module-aware resource loading
            FXMLLoader loader = new FXMLLoader(
                    Launcher.class.getResource("/com/example/themannyhub/LoginView.fxml")
            );

            if (loader.getLocation() == null) {
                System.err.println("LoginView.fxml not found!");
                return false;
            }

            Stage loginStage = new Stage();
            loginStage.initModality(Modality.APPLICATION_MODAL);
            loginStage.setTitle("Login - The Manny Hub");
            loginStage.setResizable(false);

            Scene loginScene = new Scene(loader.load(), 400, 350);
            loginStage.setScene(loginScene);

            loginStage.showAndWait();

            LoginController controller = loader.getController();
            if (controller != null && controller.isLoginSuccessful()) {
                authService = controller.getAuthService();
                return true;
            }

        } catch (Exception e) {
            System.err.println("Error loading login dialog: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}