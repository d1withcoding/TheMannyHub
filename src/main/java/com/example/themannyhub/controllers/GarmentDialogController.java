package com.example.themannyhub.controllers;

import com.example.themannyhub.models.Customer;
import com.example.themannyhub.models.Garment;
import com.example.themannyhub.models.JacketMeasurements;
import com.example.themannyhub.models.ShirtMeasurements;
import com.example.themannyhub.models.SuitMeasurements;
import com.example.themannyhub.models.TrouserMeasurements;
import com.example.themannyhub.services.GarmentService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for the garment add/edit dialog.
 * Handles form population, validation, and saving for all four garment types.
 */
public class GarmentDialogController {

    // ===== Shared =====
    @FXML private Label dialogTitle;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TabPane typeTabPane;
    @FXML private Tab trousersTab;
    @FXML private Tab shirtTab;
    @FXML private Tab jacketTab;
    @FXML private Tab suitTab;
    @FXML private TextArea notesArea;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    // ===== Trousers fields =====
    @FXML private TextField trouserWaistField;
    @FXML private TextField trouserInseamField;
    @FXML private TextField trouserHipField;
    @FXML private TextField trouserThighField;
    @FXML private TextField trouserFrontRiseField;
    @FXML private TextField trouserBackRiseField;
    @FXML private TextArea trouserFitPreferencesArea;

    // ===== Shirt fields =====
    @FXML private TextField shirtChestField;
    @FXML private TextField shirtShoulderField;
    @FXML private TextField shirtSleeveField;
    @FXML private TextField shirtNeckField;
    @FXML private TextField shirtLengthField;
    @FXML private ComboBox<String> shirtSleeveStyleCombo;
    @FXML private ComboBox<String> shirtCollarStyleCombo;

    // ===== Jacket fields =====
    @FXML private TextField jacketChestField;
    @FXML private TextField jacketShoulderField;
    @FXML private TextField jacketLengthField;
    @FXML private TextField jacketSleeveField;
    @FXML private TextField jacketWaistField;
    @FXML private ComboBox<String> jacketFitCombo;
    @FXML private ComboBox<String> jacketLapelCombo;

    // ===== Suit fields =====
    @FXML private TextField suitJacketChestField;
    @FXML private TextField suitJacketShoulderField;
    @FXML private TextField suitJacketLengthField;
    @FXML private TextField suitJacketSleeveField;
    @FXML private TextField suitTrouserWaistField;
    @FXML private TextField suitTrouserInseamField;
    @FXML private TextField suitTrouserHipField;
    @FXML private TextField suitTrouserThighField;
    @FXML private TextField suitTrouserFrontRiseField;
    @FXML private TextField suitTrouserBackRiseField;
    @FXML private ComboBox<String> suitJacketFitCombo;
    @FXML private ComboBox<String> suitLapelCombo;
    @FXML private TextArea suitTrouserFitArea;

    private Customer customer;
    private GarmentService garmentService;
    private Garment editingGarment;   // null when creating a new garment
    private boolean saved;

    /**
     * Initializes the dialog: populates combo boxes and sets default state.
     */
    @FXML
    public void initialize() {
        // Populate garment type selector
        typeComboBox.setItems(FXCollections.observableArrayList(
                "TROUSERS", "SHIRT", "JACKET", "SUIT"));
        typeComboBox.setValue("TROUSERS");

        // Populate shirt style options
        shirtSleeveStyleCombo.setItems(FXCollections.observableArrayList(
                "SHORT", "LONG", "THREE_QUARTER"));
        shirtCollarStyleCombo.setItems(FXCollections.observableArrayList(
                "SPREAD", "POINT", "BUTTON_DOWN"));

        // Populate jacket style options
        jacketFitCombo.setItems(FXCollections.observableArrayList(
                "SLIM", "REGULAR", "RELAXED"));
        jacketLapelCombo.setItems(FXCollections.observableArrayList(
                "NOTCH", "PEAK", "SHAWL"));

        // Suit reuses jacket options
        suitJacketFitCombo.setItems(jacketFitCombo.getItems());
        suitLapelCombo.setItems(jacketLapelCombo.getItems());

        // Set default selections
        shirtSleeveStyleCombo.setValue("LONG");
        shirtCollarStyleCombo.setValue("SPREAD");
        jacketFitCombo.setValue("REGULAR");
        jacketLapelCombo.setValue("NOTCH");
        suitJacketFitCombo.setValue("REGULAR");
        suitLapelCombo.setValue("NOTCH");

        // Show only the trousers tab initially
        showOnlyTab("TROUSERS");
        saved = false;
    }

    // ===== Initialization hooks (called from outside) =====

    /**
     * Sets up the dialog for creating a new garment for a customer.
     */
    public void initForCreate(Customer customer, GarmentService garmentService) {
        this.customer = customer;
        this.garmentService = garmentService;
        this.editingGarment = null;
        dialogTitle.setText("Add Garment for " + customer.getName());
        typeComboBox.setDisable(false);
        typeComboBox.setValue("TROUSERS");
        showOnlyTab("TROUSERS");
    }

    /**
     * Sets up the dialog for editing an existing garment.
     * Locks the garment type since changing it would require remapping all fields.
     */
    public void initForEdit(Garment garment, GarmentService garmentService) {
        this.editingGarment = garment;
        this.garmentService = garmentService;
        this.customer = null; // not needed for edit — garment already has customerId
        dialogTitle.setText("Edit " + garment.getGarmentType());
        typeComboBox.setValue(garment.getGarmentType());
        typeComboBox.setDisable(true);
        populateFromGarment(garment);
    }

    // ===== Type selection handling =====

    /**
     * Called when the user changes the garment type in the combo box.
     * Shows only the relevant tab for that garment type.
     */
    @FXML
    private void onTypeChanged() {
        String type = typeComboBox.getValue();
        if (type != null) {
            showOnlyTab(type);
        }
    }

    /**
     * Shows only the tab corresponding to the selected garment type.
     * Tab does not have setVisible/setManaged, so we remove and re-add tabs instead.
     */
    private void showOnlyTab(String type) {
        // Remove all tabs from the tab pane
        typeTabPane.getTabs().clear();

        // Add back only the selected tab
        switch (type) {
            case "SHIRT"  -> typeTabPane.getTabs().add(shirtTab);
            case "JACKET" -> typeTabPane.getTabs().add(jacketTab);
            case "SUIT"   -> typeTabPane.getTabs().add(suitTab);
            default       -> typeTabPane.getTabs().add(trousersTab);
        }
    }

    // ===== Save / Cancel =====

    /**
     * Validates the form, builds a garment object, and saves it via the service.
     * On success, sets the saved flag and closes the dialog.
     */
    @FXML
    private void onSaveClick() {
        try {
            Garment g = buildGarmentFromForm();
            g.validate(); // Let each subclass enforce its own measurement rules

            if (editingGarment == null) {
                garmentService.addGarment(g);
            } else {
                g.setId(editingGarment.getId());
                g.setCustomerId(editingGarment.getCustomerId());
                garmentService.updateGarment(g);
            }

            saved = true;
            close();
        } catch (IllegalArgumentException ex) {
            showAlert("Validation Error", ex.getMessage());
        } catch (IOException ex) {
            showAlert("Error", "Could not save garment: " + ex.getMessage());
        }
    }

    /**
     * Cancels the dialog without saving.
     */
    @FXML
    private void onCancelClick() {
        saved = false;
        close();
    }

    /**
     * Returns whether the user successfully saved a garment.
     */
    public boolean isSaved() {
        return saved;
    }

    /**
     * Closes the dialog window.
     */
    private void close() {
        if (onCloseCallback != null) {
            onCloseCallback.run();
        }
    }

    /**
     * Shows an error alert dialog.
     */
    private void showAlert(String title, String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    // ===== Build garment from form fields =====

    /**
     * Reads all form fields and constructs the appropriate Garment subclass.
     *
     * @return a new Garment instance (not yet persisted)
     */
    private Garment buildGarmentFromForm() {
        int customerId = (editingGarment != null)
                ? editingGarment.getCustomerId()
                : (customer != null ? customer.getId() : 0);

        String notes = notesArea.getText() == null ? "" : notesArea.getText().trim();
        String type = typeComboBox.getValue();

        return switch (type) {
            case "SHIRT" -> new ShirtMeasurements(
                    0, customerId,
                    parseDouble(shirtChestField.getText()),
                    parseDouble(shirtShoulderField.getText()),
                    parseDouble(shirtSleeveField.getText()),
                    parseDouble(shirtNeckField.getText()),
                    parseDouble(shirtLengthField.getText()),
                    shirtSleeveStyleCombo.getValue(),
                    shirtCollarStyleCombo.getValue(),
                    notes);

            case "JACKET" -> new JacketMeasurements(
                    0, customerId,
                    parseDouble(jacketChestField.getText()),
                    parseDouble(jacketShoulderField.getText()),
                    parseDouble(jacketLengthField.getText()),
                    parseDouble(jacketSleeveField.getText()),
                    parseDouble(jacketWaistField.getText()),
                    jacketFitCombo.getValue(),
                    jacketLapelCombo.getValue(),
                    notes);

            case "SUIT" -> new SuitMeasurements(
                    0, customerId,
                    parseDouble(suitJacketChestField.getText()),
                    parseDouble(suitJacketShoulderField.getText()),
                    parseDouble(suitJacketLengthField.getText()),
                    parseDouble(suitJacketSleeveField.getText()),
                    parseDouble(suitTrouserWaistField.getText()),
                    parseDouble(suitTrouserInseamField.getText()),
                    parseDouble(suitTrouserHipField.getText()),
                    parseDouble(suitTrouserThighField.getText()),
                    parseDouble(suitTrouserFrontRiseField.getText()),
                    parseDouble(suitTrouserBackRiseField.getText()),
                    suitJacketFitCombo.getValue(),
                    suitLapelCombo.getValue(),
                    suitTrouserFitArea.getText() == null
                            ? ""
                            : suitTrouserFitArea.getText().trim(),
                    notes);

            // Default: TROUSERS
            default -> new TrouserMeasurements(
                    0, customerId,
                    parseDouble(trouserWaistField.getText()),
                    parseDouble(trouserInseamField.getText()),
                    parseDouble(trouserHipField.getText()),
                    parseDouble(trouserThighField.getText()),
                    parseDouble(trouserFrontRiseField.getText()),
                    parseDouble(trouserBackRiseField.getText()),
                    trouserFitPreferencesArea.getText() == null
                            ? ""
                            : trouserFitPreferencesArea.getText().trim(),
                    notes);
        };
    }

    // ===== Populate form from existing garment =====

    /**
     * Fills the form fields with data from an existing garment for editing.
     */
    private void populateFromGarment(Garment g) {
        notesArea.setText(g.getNotes() == null ? "" : g.getNotes());

        if (g instanceof TrouserMeasurements t) {
            trouserWaistField.setText(fmt(t.getWaist()));
            trouserInseamField.setText(fmt(t.getInseam()));
            trouserHipField.setText(fmt(t.getHip()));
            trouserThighField.setText(fmt(t.getThigh()));
            trouserFrontRiseField.setText(fmt(t.getFrontRise()));
            trouserBackRiseField.setText(fmt(t.getBackRise()));
            trouserFitPreferencesArea.setText(
                    t.getFitPreferences() == null ? "" : t.getFitPreferences());

        } else if (g instanceof ShirtMeasurements s) {
            shirtChestField.setText(fmt(s.getChestWidth()));
            shirtShoulderField.setText(fmt(s.getShoulderWidth()));
            shirtSleeveField.setText(fmt(s.getSleeveLength()));
            shirtNeckField.setText(fmt(s.getNeckSize()));
            shirtLengthField.setText(fmt(s.getShirtLength()));
            shirtSleeveStyleCombo.setValue(s.getSleeveStyle());
            shirtCollarStyleCombo.setValue(s.getCollarStyle());

        } else if (g instanceof JacketMeasurements j) {
            jacketChestField.setText(fmt(j.getChestWidth()));
            jacketShoulderField.setText(fmt(j.getShoulderWidth()));
            jacketLengthField.setText(fmt(j.getJacketLength()));
            jacketSleeveField.setText(fmt(j.getSleeveLength()));
            jacketWaistField.setText(fmt(j.getWaistWidth()));
            jacketFitCombo.setValue(j.getJacketFit());
            jacketLapelCombo.setValue(j.getLapelStyle());

        } else if (g instanceof SuitMeasurements s) {
            suitJacketChestField.setText(fmt(s.getJacketChestWidth()));
            suitJacketShoulderField.setText(fmt(s.getJacketShoulderWidth()));
            suitJacketLengthField.setText(fmt(s.getJacketLength()));
            suitJacketSleeveField.setText(fmt(s.getJacketSleeveLength()));
            suitTrouserWaistField.setText(fmt(s.getTrouserWaist()));
            suitTrouserInseamField.setText(fmt(s.getTrouserInseam()));
            suitTrouserHipField.setText(fmt(s.getTrouserHip()));
            suitTrouserThighField.setText(fmt(s.getTrouserThigh()));
            suitTrouserFrontRiseField.setText(fmt(s.getTrouserFrontRise()));
            suitTrouserBackRiseField.setText(fmt(s.getTrouserBackRise()));
            suitJacketFitCombo.setValue(s.getJacketFit());
            suitLapelCombo.setValue(s.getLapelStyle());
            suitTrouserFitArea.setText(
                    s.getTrouserFitPreferences() == null
                            ? ""
                            : s.getTrouserFitPreferences());
        }
    }

    // ===== Helper methods =====

    /**
     * Safely parses a string to a double. Returns 0.0 for empty/null input.
     */
    private static double parseDouble(String s) {
        if (s == null || s.trim().isEmpty()) {
            return 0.0;
        }
        return Double.parseDouble(s.trim());
    }

    /**
     * Formats a double for display, stripping trailing ".0" for whole numbers.
     * Example: 32.0 → "32", 32.5 → "32.5"
     */
    private static String fmt(double d) {
        if (d == (long) d) {
            return Long.toString((long) d);
        }
        return Double.toString(d);
    }
    // Add this field
    private Runnable onCloseCallback;

    // Add this method
    public void setOnCloseCallback(Runnable callback) {
        this.onCloseCallback = callback;
    }

    // Replace the close() method:


// onCancelClick() should also use close()
}