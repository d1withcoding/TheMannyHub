package com.example.themannyhub.controllers;

import com.example.themannyhub.models.Customer;
import com.example.themannyhub.models.Garment;
import com.example.themannyhub.services.CustomerService;
import com.example.themannyhub.services.GarmentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class GarmentWindowController {

    @FXML private Label titleLabel;
    @FXML private ComboBox<String> customerFilterComboBox;
    @FXML private ComboBox<String> typeFilterComboBox;
    @FXML private TableView<Garment> garmentTable;
    @FXML private Label statusLabel;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    @FXML private TableColumn<Garment, Integer> idColumn;
    @FXML private TableColumn<Garment, String> customerColumn;
    @FXML private TableColumn<Garment, String> typeColumn;
    @FXML private TableColumn<Garment, String> summaryColumn;
    @FXML private TableColumn<Garment, String> notesColumn;
    @FXML private TableColumn<Garment, String> modifiedColumn;

    private GarmentService garmentService;
    private CustomerService customerService;
    private DashboardController parentDashboardController;
    private final ObservableList<Garment> garmentList = FXCollections.observableArrayList();

    public void setParentDashboardController(DashboardController controller) {
        this.parentDashboardController = controller;
    }

    @FXML
    public void initialize() {
        garmentService = new GarmentService();
        customerService = new CustomerService();

        // Type filter
        typeFilterComboBox.setItems(FXCollections.observableArrayList(
                "All", "TROUSERS", "SHIRT", "JACKET", "SUIT"));
        typeFilterComboBox.setValue("All");

        // Customer filter
        List<Customer> customers = customerService.getAllCustomers();
        ObservableList<String> customerNames = FXCollections.observableArrayList();
        customerNames.add("All Customers");
        for (Customer c : customers) {
            customerNames.add(c.getDisplayName() + " (ID:" + c.getId() + ")");
        }
        customerFilterComboBox.setItems(customerNames);
        customerFilterComboBox.setValue("All Customers");

        // Table columns
        idColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getId()).asObject());
        customerColumn.setCellValueFactory(cell -> {
            int cid = cell.getValue().getCustomerId();
            Customer c = customerService.getCustomerByID(cid);
            return new javafx.beans.property.SimpleStringProperty(
                    c != null ? c.getDisplayName() : "Unknown (ID:" + cid + ")");
        });
        typeColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getGarmentType()));
        summaryColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getMeasurementSummary()));
        notesColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getNotes() == null ? "" : cell.getValue().getNotes()));
        modifiedColumn.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getDateLastModified() == null ? ""
                                : cell.getValue().getDateLastModified().toString()));

        garmentTable.setItems(garmentList);

        editButton.setDisable(true);
        deleteButton.setDisable(true);

        garmentTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            boolean sel = n != null;
            editButton.setDisable(!sel);
            deleteButton.setDisable(!sel);
        });

        refreshTable();
    }

    @FXML
    private void onCustomerFilterChange() {
        refreshTable();
    }

    @FXML
    private void onTypeFilterChange() {
        refreshTable();
    }

    @FXML
    private void onAddGarmentClick() {
        // Pick a customer first
        List<Customer> customers = customerService.getAllCustomers();
        if (customers.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Customers",
                    "Please add a customer first.");
            return;
        }

        Customer selectedCustomer = showCustomerPicker(customers, "Select customer for new garment:");
        if (selectedCustomer == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/themannyhub/GarmentDialog.fxml"));
            Parent dialogRoot = loader.load();

            GarmentDialogController controller = loader.getController();
            controller.initForCreate(selectedCustomer, garmentService);
            controller.setOnCloseCallback(() -> {
                if (parentDashboardController != null) {
                    parentDashboardController.hideModal();
                    refreshTable();
                }
            });

            if (parentDashboardController != null) {
                parentDashboardController.showModal(dialogRoot);
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Could not open garment dialog: " + e.getMessage());
        }
    }

    @FXML
    private void onEditGarmentClick() {
        Garment selected = garmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/themannyhub/GarmentDialog.fxml"));
            Parent dialogRoot = loader.load();

            GarmentDialogController controller = loader.getController();
            controller.initForEdit(selected, garmentService);
            controller.setOnCloseCallback(() -> {
                if (parentDashboardController != null) {
                    parentDashboardController.hideModal();
                    refreshTable();
                }
            });

            if (parentDashboardController != null) {
                parentDashboardController.showModal(dialogRoot);
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Could not open garment dialog: " + e.getMessage());
        }
    }

    @FXML
    private void onDeleteGarmentClick() {
        Garment selected = garmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Garment");
        confirm.setContentText("Are you sure you want to delete this "
                + selected.getGarmentType() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                garmentService.deleteGarment(selected.getId());
                refreshTable();
                statusLabel.setText("Garment deleted");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Could not save changes: " + e.getMessage());
            }
        }
    }

    private void refreshTable() {
        List<Garment> allGarments = garmentService.getAllGarments();

        // Filter by customer
        String customerFilter = customerFilterComboBox.getValue();
        if (customerFilter != null && !"All Customers".equals(customerFilter)) {
            // Extract customer ID from string like "John Doe (1234567890) (ID:5)"
            int idStart = customerFilter.lastIndexOf("(ID:") + 4;
            int idEnd = customerFilter.lastIndexOf(")");
            if (idStart > 3 && idEnd > idStart) {
                int customerId = Integer.parseInt(customerFilter.substring(idStart, idEnd));
                allGarments.removeIf(g -> g.getCustomerId() != customerId);
            }
        }

        // Filter by type
        String typeFilter = typeFilterComboBox.getValue();
        if (typeFilter != null && !"All".equalsIgnoreCase(typeFilter)) {
            allGarments.removeIf(g -> !g.getGarmentType().equalsIgnoreCase(typeFilter));
        }

        garmentList.setAll(allGarments);
        statusLabel.setText("Showing " + allGarments.size() + " garment(s)");
    }

    private Customer showCustomerPicker(List<Customer> customers, String header) {
        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle("Select Customer");
        dialog.setHeaderText(header);

        ButtonType selectButton = new ButtonType("Select", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(selectButton, ButtonType.CANCEL);

        ListView<Customer> listView = new ListView<>();
        listView.getItems().addAll(customers);
        listView.setCellFactory(lv -> new ListCell<Customer>() {
            @Override
            protected void updateItem(Customer c, boolean empty) {
                super.updateItem(c, empty);
                if (empty || c == null) {
                    setText(null);
                } else {
                    setText(c.getDisplayName());
                }
            }
        });
        listView.setPrefHeight(200);

        dialog.getDialogPane().setContent(listView);
        dialog.setResultConverter(bt -> bt == selectButton
                ? listView.getSelectionModel().getSelectedItem() : null);

        Optional<Customer> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}