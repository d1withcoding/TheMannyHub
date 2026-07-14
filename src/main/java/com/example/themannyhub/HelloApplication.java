package com.example.themannyhub;

import com.example.themannyhub.controllers.LoginController;
import com.example.themannyhub.services.AuthService;
import com.example.themannyhub.theme.ThemeManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class HelloApplication extends Application {

    private AuthService authService;

    @Override
    public void init() {
        // Apply the application-wide Cupertino Light theme before any Scene is created.
        ThemeManager.applyToApplication();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Step 1: Show login dialog first
        if (!showLoginDialog()) {
            // User cancelled login — exit application
            System.exit(0);
            return;
        }

        // Step 2: User authenticated — show main window
        showMainWindow(primaryStage);
    }

    private boolean showLoginDialog() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("LoginView.fxml"));
        Stage loginStage = new Stage();
        loginStage.initStyle(StageStyle.UNDECORATED); // No title bar
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.setTitle("Login - The Manny Hub");
        Scene loginScene = new Scene(loader.load());
        ThemeManager.apply(loginScene);
        loginStage.setScene(loginScene);
        loginStage.setResizable(false);

        // Show and wait — blocks until login window closes
        loginStage.showAndWait();

        LoginController controller = loader.getController();
        if (controller.isLoginSuccessful()) {
            authService = controller.getAuthService();
            return true;
        }
        return false;
    }

    private void showMainWindow(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("MainWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
        ThemeManager.apply(scene);
        stage.setTitle("The Manny Hub - " +
                (authService != null ? authService.getCurrentUser().getDisplayName() : ""));
        stage.setScene(scene);
        stage.show();

        // Handle window close — logout
        stage.setOnCloseRequest(event -> {
            if (authService != null) {
                authService.logout();
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
