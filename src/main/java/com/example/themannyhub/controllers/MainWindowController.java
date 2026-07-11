package com.example.themannyhub.controllers;

import com.example.themannyhub.models.Customer;
import com.example.themannyhub.models.Status;
import com.example.themannyhub.services.CustomerService;
import com.example.themannyhub.services.GarmentService;
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

    @FXML private TextField searchTextField;
    @FXML private ComboBox<String> statusFilterComboBox;
    @FXML private TableView<Customer> customerTable;
    @FXML private Label statusLabel;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button manageGarmentsButton;

    // Table columns injected by fx:id — eliminates fragile getColumns().get(N) lookups.
    @FXML private TableColumn<Customer, Integer> idColumn;
    @FXML private TableColumn<Customer, String> nameColumn;
    @FXML private TableColumn<Customer, String> phoneColumn;
    @FXML private TableColumn<Customer, Double> waistColumn;
    @FXML private TableColumn<Customer, Double> inseamColumn;
    @FXML private TableColumn<Customer, Double> hipColumn;
    @FXML private TableColumn<Customer, Double> thighColumn;
    @FXML private TableColumn<Customer, Double> frontRiseColumn;
    @FXML private TableColumn<Customer, Double> backRiseColumn;
    @FXML private TableColumn<Customer, String> fitPreferencesColumn;
    @FXML private TableColumn<Customer, String> statusColumn;

    // ===== SERVICE LAYER =====
    private CustomerService customerService;
    private GarmentService garmentService;

    private ObservableList<Customer> customerObservableList;

    @FXML
    public void initialize() {
        customerService = new CustomerService();
        garmentService = new GarmentService();

        setupTableColumns();

        statusFilterComboBox.setItems(FXCollections.observableArrayList(
                "All Customers", "Active", "Inactive"
        ));
        statusFilterComboBox.setValue("All Customers");

        editButton.setDisable(true);
        deleteButton.setDisable(true);
        manageGarmentsButton.setDisable(true);

        // Enable Edit/Delete/Garments when a row is selected.
        customerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isRowSelected = newSelection != null;
            editButton.setDisable(!isRowSelected);
            deleteButton.setDisable(!isRowSelected);
            manageGarmentsButton.setDisable(!isRowSelected);
        });

        refreshTable();
    }

    // ===== TABLE SETUP =====
    // Cell value factories are bound by JavaFX field name -> getter via PropertyValueFactory.
    private void setupTableColumns() {
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
        refreshTable();
        statusLabel.setText("Found " + customerTable.getItems().size() + " customer(s)");
    }

    @FXML
    private void onStatusFilterChange() {
        refreshTable();
        statusLabel.setText("Showing " + customerTable.getItems().size() + " customer(s)");
    }

    @FXML
    private void onAddCustomerClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/themannyhub/CustomerDialog.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.setScene(new Scene(loader.load()));
            dialogStage.setTitle("Add Customer");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(addButton.getScene().getWindow());

            CustomerDialogController dialogController = loader.getController();
            dialogStage.showAndWait();

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
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/themannyhub/CustomerDialog.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.setScene(new Scene(loader.load()));
            dialogStage.setTitle("Edit Customer");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(editButton.getScene().getWindow());

            CustomerDialogController dialogController = loader.getController();
            dialogController.setCustomer(selectedCustomer);

            dialogStage.showAndWait();

            if (dialogController.isSaved()) {
                Customer updatedData = dialogController.getCustomerData();
                updatedData.setId(selectedCustomer.getId());
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
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Delete");
        confirmDialog.setHeaderText("Delete Customer");
        confirmDialog.setContentText("Are you sure you want to delete " + selectedCustomer.getName() + "?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                customerService.deleteCustomer(selectedCustomer.getId());
                // Cascade: also remove that customer's garments so we don't leave orphan records.
                garmentService.deleteGarmentsForCustomer(selectedCustomer.getId());
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

    @FXML
    private void onManageGarmentsClick() {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/themannyhub/GarmentWindow.fxml"));
            Stage garmentStage = new Stage();
            garmentStage.setScene(new Scene(loader.load()));
            garmentStage.setTitle("Garments for " + selectedCustomer.getName());
            garmentStage.initModality(Modality.WINDOW_MODAL);
            garmentStage.initOwner(manageGarmentsButton.getScene().getWindow());

            GarmentWindowController controller = loader.getController();
            controller.initForCustomer(selectedCustomer, garmentService);

            garmentStage.showAndWait();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open the garments window: " + e.getMessage());
        }
    }

    // ===== HELPER METHODS =====

    private void refreshTable() {
        String selectedFilter = statusFilterComboBox.getValue();
        String searchQuery = searchTextField.getText();

        List<Customer> base;
        if ("Active".equals(selectedFilter)) {
            base = customerService.filterCustomerByStatus(Status.ACTIVE);
        } else if ("Inactive".equals(selectedFilter)) {
            base = customerService.filterCustomerByStatus(Status.INACTIVE);
        } else {
            base = customerService.getAllCustomers();
        }

        List<Customer> filtered;
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String q = searchQuery.toLowerCase().trim();
            filtered = new java.util.ArrayList<>();
            for (Customer c : base) {
                if (c.getName().toLowerCase().contains(q)) {
                    filtered.add(c);
                }
            }
        } else {
            filtered = base;
        }

        customerObservableList = FXCollections.observableArrayList(filtered);
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
