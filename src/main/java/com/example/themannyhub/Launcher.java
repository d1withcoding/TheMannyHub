package com.example.themannyhub;

import com.example.themannyhub.controllers.LoginController;
import com.example.themannyhub.services.AuthService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Application entry point.
 * Shows login dialog first, then main window on successful authentication.
 */
public class Launcher extends Application {

    private AuthService authService;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Step 1: Show login dialog
        // If user cancels or closes, exit the application
        if (!showLoginDialog()) {
            System.exit(0);
            return;
        }

        // Step 2: User authenticated — load main window
        // The path starts from the resources folder
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/themannyhub/MainWindow.fxml")
        );

        // Create the scene (the content inside the window)
        Scene scene = new Scene(loader.load(), 1100, 600);

        // Set the window title with logged-in user's name
        String userName = authService.getCurrentUser() != null
                ? " - " + authService.getCurrentUser().getDisplayName()
                : "";
        primaryStage.setTitle("The Manny Hub" + userName);

        // Put the scene in the window
        primaryStage.setScene(scene);

        // Set minimum window size so it can't get too small
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(400);

        // Handle application close — logout user
        primaryStage.setOnCloseRequest(event -> {
            if (authService != null) {
                authService.logout();
            }
            System.exit(0);
        });

        // Show the window to the user
        primaryStage.show();
    }

    /**
     * Displays the login dialog as a modal window.
     * Blocks execution until the user logs in or cancels.
     *
     * @return true if login was successful, false if cancelled/closed
     */
    private boolean showLoginDialog() {
        try {
            // Load the login FXML
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/themannyhub/LoginView.fxml")
            );

            // Create a new stage (window) for login
            Stage loginStage = new Stage();

            // Make it modal — blocks interaction with other windows
            loginStage.initModality(Modality.APPLICATION_MODAL);

            // Remove window decorations (optional — for custom look)
            // Comment this out if you want the standard title bar
            // loginStage.initStyle(StageStyle.UNDECORATED);

            loginStage.setTitle("Login - The Manny Hub");
            loginStage.setResizable(false);

            // Set the scene
            Scene loginScene = new Scene(loader.load(), 400, 350);
            loginStage.setScene(loginScene);

            // Show and wait — code pauses here until login window closes
            loginStage.showAndWait();

            // Check if login was successful
            LoginController controller = loader.getController();
            if (controller != null && controller.isLoginSuccessful()) {
                // Store auth service reference for later use
                authService = controller.getAuthService();
                return true;
            }

        } catch (Exception e) {
            System.err.println("Error loading login dialog: " + e.getMessage());
            e.printStackTrace();
        }

        // Login failed or was cancelled
        return false;
    }
}