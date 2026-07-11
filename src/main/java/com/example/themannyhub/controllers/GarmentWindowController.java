package com.example.themannyhub.controllers;

import com.example.themannyhub.models.Customer;
import com.example.themannyhub.models.Garment;
import com.example.themannyhub.services.GarmentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class GarmentWindowController {

    @FXML private Label titleLabel;
    @FXML private ComboBox<String> typeFilterComboBox;
    @FXML private TableView<Garment> garmentTable;
    @FXML private Label statusLabel;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    @FXML private TableColumn<Garment, Integer> idColumn;
    @FXML private TableColumn<Garment, String> typeColumn;
    @FXML private TableColumn<Garment, String> summaryColumn;
    @FXML private TableColumn<Garment, String> notesColumn;
    @FXML private TableColumn<Garment, String> modifiedColumn;

    private GarmentService garmentService;
    private Customer customer;
    private final ObservableList<Garment> garmentList = FXCollections.observableArrayList();

    public void initForCustomer(Customer customer, GarmentService garmentService) {
        this.customer = customer;
        this.garmentService = garmentService;
        titleLabel.setText("Garments for " + customer.getName());
        refreshTable();
    }

    @FXML
    public void initialize() {
        // Type filter options. "All" shows every garment for this customer.
        typeFilterComboBox.setItems(FXCollections.observableArrayList(
                "All", "TROUSERS", "SHIRT", "JACKET", "SUIT"));
        typeFilterComboBox.setValue("All");

        // Use JavaFX property accessors that hit Garment's own getters.
        idColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()).asObject());
        typeColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getGarmentType()));
        summaryColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMeasurementSummary()));
        notesColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getNotes() == null ? "" : c.getValue().getNotes()));
        modifiedColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getDateLastModified() == null
                        ? ""
                        : c.getValue().getDateLastModified().toString()));

        garmentTable.setItems(garmentList);

        editButton.setDisable(true);
        deleteButton.setDisable(true);

        garmentTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            boolean sel = n != null;
            editButton.setDisable(!sel);
            deleteButton.setDisable(!sel);
        });
    }

    @FXML
    private void onTypeFilterChange() {
        refreshTable();
    }

    @FXML
    private void onAddGarmentClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/themannyhub/GarmentDialog.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.setScene(new Scene(loader.load()));
            dialogStage.setTitle("Add Garment for " + customer.getName());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(addButton.getScene().getWindow());

            GarmentDialogController controller = loader.getController();
            controller.initForCreate(customer, garmentService);

            dialogStage.showAndWait();
            refreshTable();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open the garment dialog: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", e.getMessage());
        }
    }

    @FXML
    private void onEditGarmentClick() {
        Garment selected = garmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/themannyhub/GarmentDialog.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.setScene(new Scene(loader.load()));
            dialogStage.setTitle("Edit Garment");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(editButton.getScene().getWindow());

            GarmentDialogController controller = loader.getController();
            controller.initForEdit(selected, garmentService);

            dialogStage.showAndWait();
            refreshTable();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open the garment dialog: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", e.getMessage());
        }
    }

    @FXML
    private void onDeleteGarmentClick() {
        Garment selected = garmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Garment");
        confirm.setContentText("Are you sure you want to delete this " + selected.getGarmentType() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                garmentService.deleteGarment(selected.getId());
                refreshTable();
                statusLabel.setText("Garment deleted");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not save changes: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
            }
        }
    }

    private void refreshTable() {
        if (customer == null || garmentService == null) {
            return;
        }
        List<Garment> garments = garmentService.getGarmentsForCustomer(customer.getId());
        String filter = typeFilterComboBox.getValue();
        if (filter != null && !"All".equalsIgnoreCase(filter)) {
            garments.removeIf(g -> !g.getGarmentType().equalsIgnoreCase(filter));
        }
        garmentList.setAll(garments);
        statusLabel.setText("Showing " + garments.size() + " garment(s)");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
