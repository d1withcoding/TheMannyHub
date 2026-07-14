package com.example.themannyhub;

import atlantafx.base.theme.CupertinoLight;
import com.example.themannyhub.controllers.DashboardController;
import com.example.themannyhub.controllers.LoginController;
import com.example.themannyhub.services.AuthService;
import com.example.themannyhub.services.CustomerService;
import com.example.themannyhub.models.Customer;
import com.example.themannyhub.theme.ThemeManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class Launcher extends Application {

    private AuthService authService;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void init() {
        // Apply Cupertino Light theme globally so every Scene picks it up.
        ThemeManager.applyToApplication();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Show login dialog first
        if (!showLoginDialog()) {
            System.exit(0);
            return;
        }

        // Show dashboard after successful login
        showDashboard();
    }

    private boolean showLoginDialog() {
        try {
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
            ThemeManager.apply(loginScene);
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

    private void showDashboard() {
        try {
            FXMLLoader dashboardLoader = new FXMLLoader(
                    Launcher.class.getResource("/com/example/themannyhub/Dashboard.fxml")
            );

            if (dashboardLoader.getLocation() == null) {
                System.err.println("Dashboard.fxml not found!");
                return;
            }

            Stage dashboardStage = new Stage();
            Scene dashboardScene = new Scene(dashboardLoader.load(), 1000, 700);
            ThemeManager.apply(dashboardScene);
            dashboardStage.setTitle("The Manny Hub - Dashboard");
            dashboardStage.setScene(dashboardScene);
            dashboardStage.setMinWidth(900);
            dashboardStage.setMinHeight(600);

            // Load initial recent customers
            DashboardController dashboardController = dashboardLoader.getController();
            CustomerService customerService = new CustomerService();
            List<Customer> allCustomers = customerService.getAllCustomers();
            if (!allCustomers.isEmpty()) {
                int limit = Math.min(8, allCustomers.size());
                dashboardController.setRecentCustomers(allCustomers.subList(0, limit));
            }

            dashboardStage.setOnCloseRequest(event -> {
                if (authService != null) {
                    authService.logout();
                }
                System.exit(0);
            });

            dashboardStage.show();

        } catch (Exception e) {
            System.err.println("Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
