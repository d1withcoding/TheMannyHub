package com.example.themannyhub.controllers;

import com.example.themannyhub.models.Customer;
import com.example.themannyhub.models.Status;
import com.example.themannyhub.services.AuthService;
import com.example.themannyhub.services.CustomerService;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DashboardController {
    @FXML private Label userLabel;
    @FXML private TextField searchField;
    @FXML private VBox searchResultsBox;
    @FXML private VBox recentCustomersList;

    private CustomerService customerService;
    private AuthService authService;
    private LinkedList<Customer> recentCustomers = new LinkedList<>();
    private PauseTransition searchDebounce;

    @FXML
    public void initialize() {
        customerService = new CustomerService();
        authService = new AuthService();

        // Set welcome message using instance method
        if (authService.getCurrentUser() != null) {
            userLabel.setText("Welcome, " + authService.getCurrentUser().getUsername());
        } else {
            userLabel.setText("Welcome, User");
        }

        // Set up search debounce
        searchDebounce = new PauseTransition(Duration.millis(300));
        searchDebounce.setOnFinished(event -> performSearch());

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchDebounce.stop();
            if (newValue.isEmpty()) {
                hideSearchResults();
            } else {
                searchDebounce.playFromStart();
            }
        });

        // Hide search results when focus leaves the search field
        searchField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                PauseTransition delay = new PauseTransition(Duration.millis(200));
                delay.setOnFinished(event -> hideSearchResults());
                delay.play();
            }
        });
    }

    public void setRecentCustomers(List<Customer> customers) {
        recentCustomers.clear();
        if (customers != null) {
            recentCustomers.addAll(customers);
        }
        refreshRecentCustomersList();
    }

    private void refreshRecentCustomersList() {
        recentCustomersList.getChildren().clear();

        if (recentCustomers.isEmpty()) {
            Label emptyLabel = new Label("No recent customers yet");
            emptyLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 14px; -fx-padding: 20 0 0 0;");
            recentCustomersList.getChildren().add(emptyLabel);
        } else {
            int count = 0;
            for (Customer customer : recentCustomers) {
                if (count >= 8) break;
                recentCustomersList.getChildren().add(createCustomerRow(customer));
                count++;
            }
        }
    }

    private VBox createCustomerRow(Customer customer) {
        VBox row = new VBox(5);
        row.setStyle("-fx-padding: 10; -fx-background-radius: 5; -fx-cursor: hand;");

        Label nameLabel = new Label(customer.getName());
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        HBox details = new HBox(10);
        Label phoneLabel = new Label(customer.getPhone());
        phoneLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        String statusColor = customer.getStatus() == Status.ACTIVE ? "#27ae60" : "#e74c3c";
        Label statusLabel = new Label(customer.getStatus().toString());
        statusLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + statusColor + ";");

        details.getChildren().addAll(phoneLabel, statusLabel);
        row.getChildren().addAll(nameLabel, details);

        row.setOnMouseEntered(event ->
                row.setStyle("-fx-padding: 10; -fx-background-radius: 5; -fx-cursor: hand; -fx-background-color: #ebf5fb;"));
        row.setOnMouseExited(event ->
                row.setStyle("-fx-padding: 10; -fx-background-radius: 5; -fx-cursor: hand;"));

        row.setOnMouseClicked(event -> openCustomerDialog(customer));

        return row;
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            hideSearchResults();
            return;
        }

        // Search by name (CustomerService.searchCustomers may not exist)
        // Use getAllCustomers and filter manually
        List<Customer> allCustomers = customerService.getAllCustomers();
        List<Customer> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        for (Customer c : allCustomers) {
            if (c.getName().toLowerCase().contains(lowerQuery) ||
                    c.getPhone().contains(lowerQuery)) {
                results.add(c);
            }
        }

        showSearchResults(results);
    }

    private void showSearchResults(List<Customer> results) {
        searchResultsBox.getChildren().clear();

        if (results.isEmpty()) {
            Label noResults = new Label("No customers found");
            noResults.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 13px; -fx-padding: 12 15 12 15;");
            searchResultsBox.getChildren().add(noResults);
        } else {
            for (Customer customer : results) {
                VBox resultRow = createCustomerRow(customer);
                resultRow.setOnMouseClicked(event -> {
                    hideSearchResults();
                    searchField.clear();
                    openCustomerDialog(customer);
                });
                searchResultsBox.getChildren().add(resultRow);
            }
        }

        searchResultsBox.setVisible(true);
        searchResultsBox.setManaged(true);
    }

    private void hideSearchResults() {
        searchResultsBox.setVisible(false);
        searchResultsBox.setManaged(false);
        searchResultsBox.getChildren().clear();
    }

    private void openCustomerDialog(Customer customer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/themannyhub/CustomerDialog.fxml"));
            Parent root = loader.load();

            CustomerDialogController controller = loader.getController();
            controller.setCustomer(customer);
            controller.setDashboardController(this);

            Stage stage = new Stage();
            stage.setTitle("Edit Customer - " + customer.getName());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            refreshAfterEdit();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshAfterEdit() {
        List<Customer> allCustomers = customerService.getAllCustomers();

        List<Customer> updatedRecents = new ArrayList<>();
        for (Customer recent : recentCustomers) {
            for (Customer fresh : allCustomers) {
                if (fresh.getPhone().equals(recent.getPhone())) {
                    updatedRecents.add(fresh);
                    break;
                }
            }
        }
        recentCustomers.clear();
        recentCustomers.addAll(updatedRecents);
        refreshRecentCustomersList();
    }

    @FXML
    private void addNewCustomer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/themannyhub/CustomerDialog.fxml"));
            Parent root = loader.load();

            CustomerDialogController controller = loader.getController();
            controller.setDashboardController(this);

            Stage stage = new Stage();
            stage.setTitle("Add New Customer");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            refreshAfterEdit();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addNewGarment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/themannyhub/GarmentDialog.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add New Garment");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openCustomerManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/themannyhub/MainWindow.fxml"));
            Parent root = loader.load();

            MainWindowController controller = loader.getController();
            controller.setDashboardController(this);

            Stage stage = new Stage();
            stage.setTitle("Customer Management");
            stage.setScene(new Scene(root, 1100, 600));
            stage.setMinWidth(800);
            stage.setMinHeight(400);
            stage.initModality(Modality.NONE);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openGarmentManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/themannyhub/GarmentWindow.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Garment Management");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        // Use instance method
        authService.logout();

        Stage stage = (Stage) userLabel.getScene().getWindow();
        stage.close();

        Platform.runLater(() -> {
            try {
                FXMLLoader loginLoader = new FXMLLoader(
                        getClass().getResource("/com/example/themannyhub/LoginView.fxml"));
                Parent loginRoot = loginLoader.load();

                Stage loginStage = new Stage();
                loginStage.initModality(Modality.APPLICATION_MODAL);
                loginStage.setTitle("Login - The Manny Hub");
                loginStage.setResizable(false);
                loginStage.setScene(new Scene(loginRoot, 400, 350));
                loginStage.showAndWait();

                LoginController loginController = loginLoader.getController();
                if (loginController != null && loginController.isLoginSuccessful()) {
                    refreshDashboard();
                } else {
                    Platform.exit();
                }

            } catch (IOException e) {
                e.printStackTrace();
                Platform.exit();
            }
        });
    }

    private void refreshDashboard() {
        List<Customer> allCustomers = customerService.getAllCustomers();
        if (!allCustomers.isEmpty()) {
            setRecentCustomers(allCustomers.subList(0, Math.min(8, allCustomers.size())));
        }

        if (authService.getCurrentUser() != null) {
            userLabel.setText("Welcome, " + authService.getCurrentUser().getUsername());
        } else {
            userLabel.setText("Welcome, User");
        }
    }

    public void addRecentCustomer(Customer customer) {
        recentCustomers.removeIf(c -> c.getPhone().equals(customer.getPhone()));
        recentCustomers.addFirst(customer);
        while (recentCustomers.size() > 8) {
            recentCustomers.removeLast();
        }
        refreshRecentCustomersList();
    }
}