package com.example.themannyhub.controllers;

import com.example.themannyhub.models.Customer;
import com.example.themannyhub.models.Status;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class DashboardHomeController {

    @FXML private TextField searchField;
    @FXML private VBox searchResultsBox;
    @FXML private VBox recentCustomersList;

    private DashboardController parentDashboardController;
    private PauseTransition searchDebounce;

    @FXML
    public void initialize() {
        searchDebounce = new PauseTransition(Duration.millis(300));
        searchDebounce.setOnFinished(e -> performSearch());
        searchField.textProperty().addListener((obs, old, newVal) -> {
            searchDebounce.stop();
            if (newVal.isEmpty()) {
                hideSearchResults();
            } else {
                searchDebounce.playFromStart();
            }
        });
    }

    public void setParentDashboardController(DashboardController controller) {
        this.parentDashboardController = controller;
    }

    public void setRecentCustomers(List<Customer> customers) {
        recentCustomersList.getChildren().clear();
        if (customers == null || customers.isEmpty()) {
            Label empty = new Label("No recent customers yet");
            empty.setStyle("-fx-text-fill: -color-fg-subtle; -fx-font-size: 13px; -fx-padding: 15 0 0 0;");
            recentCustomersList.getChildren().add(empty);
        } else {
            for (Customer c : customers) {
                recentCustomersList.getChildren().add(createCustomerRow(c));
            }
        }
    }

    private VBox createCustomerRow(Customer customer) {
        VBox row = new VBox(5);
        row.setStyle("-fx-padding: 10; -fx-background-radius: 5; -fx-cursor: hand;");

        Label nameLabel = new Label(customer.getName());
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        HBox details = new HBox(10);
        Label phoneLabel = new Label(customer.getPhone());
        phoneLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: -color-fg-subtle;");

        String color = customer.getStatus() == Status.ACTIVE ? "-color-success-emphasis" : "-color-danger-emphasis";
        Label statusLabel = new Label(customer.getStatus().toString());
        statusLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        details.getChildren().addAll(phoneLabel, statusLabel);
        row.getChildren().addAll(nameLabel, details);

        row.setOnMouseEntered(e -> row.setStyle(
                "-fx-padding: 10; -fx-background-radius: 5; -fx-cursor: hand; -fx-background-color: -color-neutral-subtle;"));
        row.setOnMouseExited(e -> row.setStyle(
                "-fx-padding: 10; -fx-background-radius: 5; -fx-cursor: hand;"));
        row.setOnMouseClicked(e -> {
            if (parentDashboardController != null) {
                parentDashboardController.openCustomerDialog(customer);
            }
        });

        return row;
    }

    private void performSearch() {
        if (parentDashboardController == null) return;
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            hideSearchResults();
            return;
        }
        List<Customer> all = parentDashboardController.getCustomerService().getAllCustomers();
        List<Customer> results = new ArrayList<>();
        String lower = query.toLowerCase();
        for (Customer c : all) {
            if (c.getName().toLowerCase().contains(lower) || c.getPhone().contains(lower)) {
                results.add(c);
            }
        }
        showSearchResults(results);
    }

    private void showSearchResults(List<Customer> results) {
        searchResultsBox.getChildren().clear();
        if (results.isEmpty()) {
            Label noResults = new Label("No customers found");
            noResults.setStyle("-fx-text-fill: -color-fg-subtle; -fx-font-size: 13px; -fx-padding: 12 15 12 15;");
            searchResultsBox.getChildren().add(noResults);
        } else {
            for (Customer c : results) {
                VBox row = createCustomerRow(c);
                row.setOnMouseClicked(e -> {
                    hideSearchResults();
                    searchField.clear();
                    parentDashboardController.openCustomerDialog(c);
                });
                searchResultsBox.getChildren().add(row);
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

    @FXML
    private void addNewCustomer() {
        if (parentDashboardController != null) parentDashboardController.addNewCustomer();
    }

    @FXML
    private void addNewGarment() {
        if (parentDashboardController != null) parentDashboardController.addNewGarment();
    }

    @FXML
    private void openCustomerManagement() {
        if (parentDashboardController != null) parentDashboardController.onCustomersNavClick();
    }

    @FXML
    private void openGarmentManagement() {
        if (parentDashboardController != null) parentDashboardController.onGarmentsNavClick();
    }
}