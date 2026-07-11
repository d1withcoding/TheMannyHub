package com.example.themannyhub.controllers;

import com.example.themannyhub.models.Customer;
import com.example.themannyhub.models.Status;
import com.example.themannyhub.utils.ValidationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class CustomerDialogController {

    // ===== FXML INJECTED FIELDS =====

    @FXML private Label dialogTitle;

    // Input fields
    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField waistField;
    @FXML private TextField inseamField;
    @FXML private TextField hipField;
    @FXML private TextField thighField;
    @FXML private TextField frontRiseField;
    @FXML private TextField backRiseField;
    @FXML private TextArea fitPreferencesArea;
    @FXML private ComboBox<Status> statusComboBox;

    // Error labels
    @FXML private Label nameErrorLabel;
    @FXML private Label phoneErrorLabel;
    @FXML private Label waistErrorLabel;
    @FXML private Label inseamErrorLabel;
    @FXML private Label hipErrorLabel;
    @FXML private Label thighErrorLabel;
    @FXML private Label frontRiseErrorLabel;
    @FXML private Label backRiseErrorLabel;
    @FXML private Label fitPreferencesErrorLabel;
    @FXML private Label statusErrorLabel;

    // Buttons
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    // ===== STATE FIELDS =====

    private Customer customer;      // The customer being edited (null if adding new)
    private boolean isEditMode;     // True = editing, False = adding
    private boolean saved;          // True if user clicked Save

    // ===== INITIALIZE METHOD =====

    @FXML
    public void initialize() {
        // Populate status dropdown
        statusComboBox.getItems().setAll(Status.ACTIVE, Status.INACTIVE);
        statusComboBox.setValue(Status.ACTIVE); // Default to ACTIVE

        // Default state
        saved = false;
        isEditMode = false;
    }

    // ===== PUBLIC METHODS CALLED BY MAIN WINDOW CONTROLLER =====

    public void setCustomer(Customer customer) {
        this.customer = customer;
        this.isEditMode = true;

        // Change title
        dialogTitle.setText("Edit Customer");

        // Pre-fill all fields with customer data
        nameField.setText(customer.getName());
        phoneField.setText(customer.getPhone());
        waistField.setText(String.valueOf(customer.getWaist()));
        inseamField.setText(String.valueOf(customer.getInseam()));
        hipField.setText(String.valueOf(customer.getHip()));
        thighField.setText(String.valueOf(customer.getThigh()));
        frontRiseField.setText(String.valueOf(customer.getFrontRise()));
        backRiseField.setText(String.valueOf(customer.getBackRise()));
        fitPreferencesArea.setText(customer.getFitPreferences());
        statusComboBox.setValue(customer.getStatus());
    }

    public boolean isSaved() {
        return saved;
    }

    public Customer getCustomerData() {
        // Create a Customer object from form data
        // ID will be set by the calling code (auto-generated for new, kept for edits)
        return new Customer(
                0, // ID placeholder (will be overridden for edits, auto-generated for new)
                nameField.getText().trim(),
                phoneField.getText().trim(),
                parseDouble(waistField.getText()),
                parseDouble(inseamField.getText()),
                parseDouble(hipField.getText()),
                parseDouble(thighField.getText()),
                parseDouble(frontRiseField.getText()),
                parseDouble(backRiseField.getText()),
                fitPreferencesArea.getText().trim(),
                statusComboBox.getValue()
        );
    }

    // ===== EVENT HANDLERS =====

    @FXML
    private void onSaveClick() {
        // Collect all form values
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String waistText = waistField.getText().trim();
        String inseamText = inseamField.getText().trim();
        String hipText = hipField.getText().trim();
        String thighText = thighField.getText().trim();
        String frontRiseText = frontRiseField.getText().trim();
        String backRiseText = backRiseField.getText().trim();
        String fitPreferences = fitPreferencesArea.getText().trim();
        Status status = statusComboBox.getValue();

        // Parse numeric fields (with error handling for non-numeric input)
        double waist = 0, inseam = 0, hip = 0, thigh = 0, frontRise = 0, backRise = 0;
        try {
            waist = parseDouble(waistText);
            inseam = parseDouble(inseamText);
            hip = parseDouble(hipText);
            thigh = parseDouble(thighText);
            frontRise = parseDouble(frontRiseText);
            backRise = parseDouble(backRiseText);
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid numbers for measurement fields.");
            return;
        }

        // Validate all fields
        String validationError = ValidationUtil.validateAllCustomerFields(
                name, phone, waist, inseam, hip, thigh,
                frontRise, backRise, fitPreferences, status
        );

        if (!validationError.isEmpty()) {
            showAlert("Validation Error", validationError);
            return;
        }

        // All good - save and close
        saved = true;

        // Add to recent customers on dashboard (if dashboard is available)
        if (dashboardController != null) {
            Customer savedCustomer = getCustomerData();
            dashboardController.addRecentCustomer(savedCustomer);
        }

        closeDialog();
    }
    @FXML
    private void onCancelClick() {
        saved = false;
        closeDialog();
    }

    // ===== REAL-TIME FIELD VALIDATION =====
    // These methods hide error labels when the user starts typing

    @FXML
    private void onNameFieldChanged() {
        hideError(nameErrorLabel);
    }

    @FXML
    private void onPhoneFieldChanged() {
        hideError(phoneErrorLabel);
    }

    @FXML
    private void onWaistFieldChanged() {
        hideError(waistErrorLabel);
    }

    @FXML
    private void onInseamFieldChanged() {
        hideError(inseamErrorLabel);
    }

    @FXML
    private void onHipFieldChanged() {
        hideError(hipErrorLabel);
    }

    @FXML
    private void onThighFieldChanged() {
        hideError(thighErrorLabel);
    }

    @FXML
    private void onFrontRiseFieldChanged() {
        hideError(frontRiseErrorLabel);
    }

    @FXML
    private void onBackRiseFieldChanged() {
        hideError(backRiseErrorLabel);
    }

    @FXML
    private void onFitPreferencesFieldChanged() {
        hideError(fitPreferencesErrorLabel);
    }

    @FXML
    private void onStatusSelectionChanged() {
        hideError(statusErrorLabel);
    }

    // ===== HELPER METHODS =====

    private double parseDouble(String text) {
        if (text == null || text.isEmpty()) {
            return 0.0;
        }
        return Double.parseDouble(text);
    }

    private void hideError(Label errorLabel) {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    private void closeDialog() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private DashboardController dashboardController;

    public void setDashboardController(DashboardController controller) {
        this.dashboardController = controller;
    }

// Inside the save method, right before closing the dialog:

}