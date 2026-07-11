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
    private LinkedList<Customer> recentCustomers = new LinkedList<>();
    private PauseTransition searchDebounce;

    @FXML
    public void initialize() {
        customerService = new CustomerService();

        // Set up user greeting
        String username = AuthService.getCurrentUser() != null ?
                AuthService.getCurrentUser().getUsername() : "User";
        userLabel.setText("Welcome, " + username);

        // Set up search with debounce
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

        // Hide search results when clicking elsewhere
        searchField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                // Delay hiding to allow clicking on results
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
                if (count >= 8) break; // Show max 8 recent customers
                recentCustomersList.getChildren().add(createCustomerRow(customer));
                count++;
            }
        }
    }

    private VBox createCustomerRow(Customer customer) {
        VBox row = new VBox(5);
        row.setStyle("-fx-padding: 10; -fx-background-radius: 5; -fx-cursor: hand;");

        // Name
        Label nameLabel = new Label(customer.getName());
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Phone and Status
        HBox details = new HBox(10);
        Label phoneLabel = new Label(customer.getPhone());
        phoneLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        String statusColor = customer.getStatus() == Status.ACTIVE ? "#27ae60" : "#e74c3c";
        Label statusLabel = new Label(customer.getStatus().toString());
        statusLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + statusColor + ";");

        details.getChildren().addAll(phoneLabel, statusLabel);
        row.getChildren().addAll(nameLabel, details);

        // Hover effects
        row.setOnMouseEntered(event ->
                row.setStyle("-fx-padding: 10; -fx-background-radius: 5; -fx-cursor: hand; -fx-background-color: #ebf5fb;"));
        row.setOnMouseExited(event ->
                row.setStyle("-fx-padding: 10; -fx-background-radius: 5; -fx-cursor: hand;"));

        // Click to open customer
        row.setOnMouseClicked(event -> openCustomerDialog(customer));

        return row;
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            hideSearchResults();
            return;
        }

        List<Customer> results = customerService.searchCustomers(query);
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

            // Refresh after dialog closes
            refreshAfterEdit();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshAfterEdit() {
        // Reload all customers to get updated data
        List<Customer> allCustomers = customerService.getAllCustomers();

        // Update recent customers with fresh data
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
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

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
        AuthService.logout();

        // Close dashboard
        Stage stage = (Stage) userLabel.getScene().getWindow();
        stage.close();

        // Show login again
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/themannyhub/LoginView.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("The Manny Hub - Login");
            loginStage.setScene(new Scene(root));
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addRecentCustomer(Customer customer) {
        // Remove if already in list to avoid duplicates
        recentCustomers.removeIf(c -> c.getPhone().equals(customer.getPhone()));
        // Add to front
        recentCustomers.addFirst(customer);
        // Keep only last 8
        while (recentCustomers.size() > 8) {
            recentCustomers.removeLast();
        }
        refreshRecentCustomersList();
    }
}