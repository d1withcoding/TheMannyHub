package com.example.themannyhub.controllers;

import com.example.themannyhub.services.AuthService;
import com.example.themannyhub.utils.ValidationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label usernameError;
    @FXML private Label passwordError;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

    private AuthService authService;
    private boolean loginSuccessful = false;

    @FXML
    public void initialize() {
        authService = new AuthService();

        // Clear errors on typing
        usernameField.textProperty().addListener((obs, old, newVal) ->
                clearErrors());
        passwordField.textProperty().addListener((obs, old, newVal) ->
                clearErrors());

        // Allow Enter key to trigger login
        passwordField.setOnAction(event -> onLoginClick());
    }

    @FXML
    private void onLoginClick() {
        clearErrors();
        boolean valid = true;

        // Validate username
        if (!ValidationUtil.validateUsername(usernameField.getText())) {
            usernameError.setText("Username must be 3-20 characters");
            usernameError.setVisible(true);
            valid = false;
        }

        // Validate password
        if (!ValidationUtil.validatePassword(passwordField.getText())) {
            passwordError.setText("Password must be at least 6 characters");
            passwordError.setVisible(true);
            valid = false;
        }

        if (!valid) return;

        // Attempt authentication
        if (authService.authenticate(usernameField.getText(),
                passwordField.getText())) {
            loginSuccessful = true;
            closeDialog();
        } else {
            errorLabel.setText("Invalid username or password");
        }
    }

    @FXML
    private void onCancelClick() {
        loginSuccessful = false;
        closeDialog();
    }

    private void clearErrors() {
        usernameError.setVisible(false);
        passwordError.setVisible(false);
        errorLabel.setText("");
    }

    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }

    public AuthService getAuthService() {
        return authService;
    }

    private void closeDialog() {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.close();
    }
}