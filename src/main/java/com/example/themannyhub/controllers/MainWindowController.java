package com.example.themannyhub.controllers;

import com.example.themannyhub.models.Customer;
import com.example.themannyhub.models.Status;
import com.example.themannyhub.services.CustomerService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class MainWindowController {

    // ===== FXML INJECTED FIELDS =====
    // These are automatically linked to the FXML file elements by JavaFX

    @FXML private TextField searchTextField;
    @FXML private ComboBox<String> statusFilterComboBox;
    @FXML private TableView<Customer> customerTable;
    @FXML private Label statusLabel;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    // ===== SERVICE LAYER =====
    // The manager that handles all business logic
    private CustomerService customerService;

    // ===== OBSERVABLE LIST =====
    // This list is connected to the table - when it changes, the table updates automatically
    private ObservableList<Customer> customerObservableList;

    // ===== INITIALIZE METHOD =====
    // Runs automatically after FXML is loaded
    // Sets up the table, filters, and loads initial data

    @FXML
    public void initialize() {
        // Create the service layer
        customerService = new CustomerService();

        // Set up the table columns
        setupTableColumns();

        // Set up the status filter dropdown
        statusFilterComboBox.setItems(FXCollections.observableArrayList(
                "All Customers", "Active", "Inactive"
        ));
        statusFilterComboBox.setValue("All Customers"); // Default selection

        // Disable Edit and Delete buttons until a row is selected
        editButton.setDisable(true);
        deleteButton.setDisable(true);

        // Enable Edit/Delete buttons when user selects a row
        customerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isRowSelected = newSelection != null;
            editButton.setDisable(!isRowSelected);
            deleteButton.setDisable(!isRowSelected);
        });

        // Load all customers into the table
        refreshTable();
    }

    // ===== TABLE SETUP =====

    private void setupTableColumns() {
        // Get the table columns (they are in order as defined in FXML)
        TableColumn<Customer, Integer> idColumn = (TableColumn<Customer, Integer>) customerTable.getColumns().get(0);
        TableColumn<Customer, String> nameColumn = (TableColumn<Customer, String>) customerTable.getColumns().get(1);
        TableColumn<Customer, String> phoneColumn = (TableColumn<Customer, String>) customerTable.getColumns().get(2);
        TableColumn<Customer, Double> waistColumn = (TableColumn<Customer, Double>) customerTable.getColumns().get(3);
        TableColumn<Customer, Double> inseamColumn = (TableColumn<Customer, Double>) customerTable.getColumns().get(4);
        TableColumn<Customer, Double> hipColumn = (TableColumn<Customer, Double>) customerTable.getColumns().get(5);
        TableColumn<Customer, Double> thighColumn = (TableColumn<Customer, Double>) customerTable.getColumns().get(6);
        TableColumn<Customer, Double> frontRiseColumn = (TableColumn<Customer, Double>) customerTable.getColumns().get(7);
        TableColumn<Customer, Double> backRiseColumn = (TableColumn<Customer, Double>) customerTable.getColumns().get(8);
        TableColumn<Customer, String> fitPreferencesColumn = (TableColumn<Customer, String>) customerTable.getColumns().get(9);
        TableColumn<Customer, String> statusColumn = (TableColumn<Customer, String>) customerTable.getColumns().get(10);

        // Link each column to the Customer object's getter method
        // PropertyValueFactory uses the field name to call the getter
        // Example: "name" calls customer.getName()
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        waistColumn.setCellValueFactory(new PropertyValueFactory<>("waist"));
        inseamColumn.setCellValueFactory(new PropertyValueFactory<>("inseam"));
        hipColumn.setCellValueFactory(new PropertyValueFactory<>("hip"));
        thighColumn.setCellValueFactory(new PropertyValueFactory<>("thigh"));
        frontRiseColumn.setCellValueFactory(new PropertyValueFactory<>("frontRise"));
        backRiseColumn.setCellValueFactory(new PropertyValueFactory<>("backRise"));
        fitPreferencesColumn.setCellValueFactory(new PropertyValueFactory<>("fitPreferences"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    // ===== EVENT HANDLERS =====

    @FXML
    private void onSearchTextChange() {
        String query = searchTextField.getText();
        List<Customer> results = customerService.searchCustomersByName(query);
        customerObservableList = FXCollections.observableArrayList(results);
        customerTable.setItems(customerObservableList);
        statusLabel.setText("Found " + results.size() + " customer(s)");
    }

    @FXML
    private void onStatusFilterChange() {
        String selected = statusFilterComboBox.getValue();
        List<Customer> results;

        if ("Active".equals(selected)) {
            results = customerService.filterCustomerByStatus(Status.ACTIVE);
        } else if ("Inactive".equals(selected)) {
            results = customerService.filterCustomerByStatus(Status.INACTIVE);
        } else {
            // "All Customers" or any other value
            results = customerService.getAllCustomers();
        }

        customerObservableList = FXCollections.observableArrayList(results);
        customerTable.setItems(customerObservableList);
        statusLabel.setText("Showing " + results.size() + " customer(s)");
    }

    @FXML
    private void onAddCustomerClick() {
        try {
            // Load the dialog FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/themannyhub/CustomerDialog.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.setScene(new Scene(loader.load()));
            dialogStage.setTitle("Add Customer");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(addButton.getScene().getWindow());

            // Get the dialog controller
            CustomerDialogController dialogController = loader.getController();

            // Show dialog and wait for user to close it
            dialogStage.showAndWait();

            // Check if user clicked Save
            if (dialogController.isSaved()) {
                Customer customerData = dialogController.getCustomerData();
                Customer newCustomer = customerService.addCustomer(
                        customerData.getName(),
                        customerData.getPhone(),
                        customerData.getWaist(),
                        customerData.getInseam(),
                        customerData.getHip(),
                        customerData.getThigh(),
                        customerData.getFrontRise(),
                        customerData.getBackRise(),
                        customerData.getFitPreferences(),
                        customerData.getStatus()
                );
                refreshTable();
                statusLabel.setText("Customer added: " + newCustomer.getName());
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not save data. Please check file permissions.");
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    @FXML
    private void onEditCustomerClick() {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();

        if (selectedCustomer == null) {
            return; // Should not happen (button is disabled when nothing selected)
        }

        try {
            // Load the dialog FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/themannyhub/CustomerDialog.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.setScene(new Scene(loader.load()));
            dialogStage.setTitle("Edit Customer");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(editButton.getScene().getWindow());

            // Get the dialog controller and pass the selected customer
            CustomerDialogController dialogController = loader.getController();
            dialogController.setCustomer(selectedCustomer);

            // Show dialog and wait
            dialogStage.showAndWait();

            // Check if user clicked Save
            if (dialogController.isSaved()) {
                Customer updatedData = dialogController.getCustomerData();
                updatedData.setId(selectedCustomer.getId()); // Keep the same ID
                customerService.updateCustomer(updatedData);
                refreshTable();
                statusLabel.setText("Customer updated: " + updatedData.getName());
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not save data. Please check file permissions.");
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    @FXML
    private void onDeleteCustomerClick() {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();

        if (selectedCustomer == null) {
            return; // Should not happen
        }

        // Show confirmation dialog
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Delete");
        confirmDialog.setHeaderText("Delete Customer");
        confirmDialog.setContentText("Are you sure you want to delete " + selectedCustomer.getName() + "?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                customerService.deleteCustomer(selectedCustomer.getId());
                refreshTable();
                statusLabel.setText("Customer deleted: " + selectedCustomer.getName());
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not save changes. Please check file permissions.");
            } catch (IllegalArgumentException e) {
                showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred: " + e.getMessage());
            }
        }
    }

    // ===== HELPER METHODS =====

    private void refreshTable() {
        // Apply current filter
        String selectedFilter = statusFilterComboBox.getValue();
        List<Customer> customers;

        if ("Active".equals(selectedFilter)) {
            customers = customerService.filterCustomerByStatus(Status.ACTIVE);
        } else if ("Inactive".equals(selectedFilter)) {
            customers = customerService.filterCustomerByStatus(Status.INACTIVE);
        } else {
            customers = customerService.getAllCustomers();
        }

        // Also apply search if there's text in search field
        String searchQuery = searchTextField.getText();
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            customers = customerService.searchCustomersByName(searchQuery);
        }

        customerObservableList = FXCollections.observableArrayList(customers);
        customerTable.setItems(customerObservableList);
        statusLabel.setText("Total customers: " + customerService.getCustomerCount());
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}