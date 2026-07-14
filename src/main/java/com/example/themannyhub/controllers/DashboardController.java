package com.example.themannyhub.controllers;

import com.example.themannyhub.models.Customer;
import com.example.themannyhub.models.Status;
import com.example.themannyhub.services.AuthService;
import com.example.themannyhub.services.CustomerService;
import com.example.themannyhub.services.GarmentService;
import com.example.themannyhub.theme.ThemeManager;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class DashboardController {

    // ===== FXML Injected Fields =====
    @FXML private Label userLabel;
    @FXML private TextField searchField;
    @FXML private VBox searchResultsBox;
    @FXML private VBox recentCustomersList;
    @FXML private StackPane contentArea;
    @FXML private StackPane modalOverlay;
    @FXML private Button dashboardNavBtn;
    @FXML private Button customersNavBtn;
    @FXML private Button garmentsNavBtn;

    // ===== Services =====
    private CustomerService customerService;
    private GarmentService garmentService;
    private AuthService authService;

    // ===== State =====
    private LinkedList<Customer> recentCustomers = new LinkedList<>();
    private PauseTransition searchDebounce;
    private Parent dashboardHomeView;  // cached dashboard home content

    // ===== Initialization =====
    @FXML
    public void initialize() {
        customerService = new CustomerService();
        garmentService = new GarmentService();
        authService = new AuthService();

        if (authService.getCurrentUser() != null) {
            userLabel.setText("Welcome, " + authService.getCurrentUser().getUsername());
        } else {
            userLabel.setText("Welcome, User");
        }

        // Load dashboard home view after FXML is loaded
        Platform.runLater(() -> loadDashboardHome());
    }

    // ===== Navigation =====

    @FXML
    private void onDashboardNavClick() {
        highlightNavButton(dashboardNavBtn);
        loadDashboardHome();
    }

    @FXML
    public void onCustomersNavClick() {
        highlightNavButton(customersNavBtn);
        loadCustomerManagement();
    }

    @FXML
    public void onGarmentsNavClick() {
        highlightNavButton(garmentsNavBtn);
        loadGarmentManagement();
    }

// DELETE showCustomerPickerForGarments() entirely - no longer needed

    private void highlightNavButton(Button active) {
        dashboardNavBtn.getStyleClass().remove("sidebar-button-active");
        customersNavBtn.getStyleClass().remove("sidebar-button-active");
        garmentsNavBtn.getStyleClass().remove("sidebar-button-active");

        active.getStyleClass().add("sidebar-button-active");
    }

    // ===== View Loading =====

    private void loadDashboardHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/themannyhub/DashboardHome.fxml"));
            Parent homeView = loader.load();
            DashboardHomeController homeController = loader.getController();
            homeController.setParentDashboardController(this);

            contentArea.getChildren().setAll(homeView);

            // Refresh recent customers in the home view
            List<Customer> all = customerService.getAllCustomers();
            if (!all.isEmpty()) {
                homeController.setRecentCustomers(
                        all.subList(0, Math.min(8, all.size())));
            }

            // Refresh metrics
            homeController.refreshMetrics();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadCustomerManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/themannyhub/MainWindow.fxml"));
            Parent customerView = loader.load();
            MainWindowController controller = loader.getController();
            controller.setDashboardController(this);

            contentArea.getChildren().setAll(customerView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadGarmentManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/themannyhub/GarmentWindow.fxml"));
            Parent garmentView = loader.load();
            GarmentWindowController controller = loader.getController();
            controller.setParentDashboardController(this);

            contentArea.getChildren().setAll(garmentView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===== Customer Picker for Garment Management =====

    private void showCustomerPickerForGarments() {
        List<Customer> allCustomers = customerService.getAllCustomers();

        if (allCustomers.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Customers");
            alert.setHeaderText(null);
            alert.setContentText("Please add a customer first before managing garments.");
            alert.showAndWait();
            return;
        }

        // Build a simple choice dialog
        ChoiceDialog<Customer> dialog = new ChoiceDialog<>(allCustomers.get(0), allCustomers);
        dialog.setTitle("Select Customer");
        dialog.setHeaderText("Choose a customer to view garments for:");
        dialog.setContentText("Customer:");

        // Customize how customer names appear in the dropdown
        // ChoiceDialog uses toString(), so we can't easily customize without a custom dialog.
        // Instead, let's use a ListView approach:
        Dialog<Customer> customDialog = new Dialog<>();
        customDialog.setTitle("Select Customer");
        customDialog.setHeaderText("Choose a customer to view garments for:");

        ButtonType selectButtonType = new ButtonType("Select", ButtonBar.ButtonData.OK_DONE);
        customDialog.getDialogPane().getButtonTypes().addAll(selectButtonType, ButtonType.CANCEL);

        ListView<Customer> listView = new ListView<>();
        listView.getItems().addAll(allCustomers);
        listView.setCellFactory(lv -> new ListCell<Customer>() {
            @Override
            protected void updateItem(Customer c, boolean empty) {
                super.updateItem(c, empty);
                if (empty || c == null) {
                    setText(null);
                } else {
                    setText(c.getName() + " (" + c.getPhone() + ")");
                }
            }
        });

        customDialog.getDialogPane().setContent(listView);
        customDialog.setResultConverter(buttonType -> {
            if (buttonType == selectButtonType) {
                return listView.getSelectionModel().getSelectedItem();
            }
            return null;
        });


    }



    // ===== In-Window Modal Overlay =====

    /**
     * Shows a modal overlay with the given content on top of the main view.
     * The overlay dims the background and centers the dialog card.
     */
    public void showModal(Parent modalContent) {
        // Wrap content in a scrollable container
        ScrollPane scrollPane = new ScrollPane(modalContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.setMaxWidth(550);
        scrollPane.setMaxHeight(650);

        VBox card = new VBox(scrollPane);
        card.setStyle("-fx-background-color: -color-bg-default; -fx-background-radius: 12; " +
                "-fx-padding: 20; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 20, 0, 0, 8);");
        card.setMaxWidth(570);
        card.setMaxHeight(670);

        // Create overlay
        StackPane overlay = new StackPane(card);
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.4);");
        overlay.setAlignment(javafx.geometry.Pos.CENTER);
        overlay.setPickOnBounds(true);

        // Click on dark background to close
        overlay.setOnMouseClicked(e -> {
            if (e.getTarget() == overlay) {
                hideModal();
            }
        });

        // Add overlay on top of content
        contentArea.getChildren().add(overlay);
    }

    public void hideModal() {
        // Remove the top layer (modal overlay) if present
        if (contentArea.getChildren().size() > 1) {
            contentArea.getChildren().remove(contentArea.getChildren().size() - 1);
        }
    }

    // ===== Actions (called from sidebar and dashboard home) =====

    @FXML
    public void addNewCustomer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/themannyhub/CustomerDialog.fxml"));
            Parent dialogRoot = loader.load();

            CustomerDialogController controller = loader.getController();
            controller.setDashboardController(this);
            controller.setOnCloseCallback(() -> hideModal());

            showModal(dialogRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addNewGarment() {
        // Need to pick a customer first for new garment
        List<Customer> allCustomers = customerService.getAllCustomers();
        if (allCustomers.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Customers");
            alert.setHeaderText(null);
            alert.setContentText("Please add a customer first before adding a garment.");
            alert.showAndWait();
            return;
        }

        // Use the same picker approach
        ChoiceDialog<Customer> dialog = new ChoiceDialog<>(allCustomers.get(0), allCustomers);
        dialog.setTitle("Select Customer");
        dialog.setHeaderText("Choose a customer for the new garment:");
        dialog.setContentText("Customer:");

        Optional<Customer> result = dialog.showAndWait();
        result.ifPresent(customer -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(
                        "/com/example/themannyhub/GarmentDialog.fxml"));
                Parent dialogRoot = loader.load();

                GarmentDialogController controller = loader.getController();
                controller.initForCreate(customer, garmentService);
                controller.setOnCloseCallback(() -> hideModal());

                showModal(dialogRoot);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Opens the customer edit dialog for a specific customer (from search/recent clicks).
     */
    public void openCustomerDialog(Customer customer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/themannyhub/CustomerDialog.fxml"));
            Parent dialogRoot = loader.load();

            CustomerDialogController controller = loader.getController();
            controller.setCustomer(customer);
            controller.setDashboardController(this);
            controller.setOnCloseCallback(() -> hideModal());

            showModal(dialogRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===== Search =====

    private void performSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            hideSearchResults();
            return;
        }
        List<Customer> all = customerService.getAllCustomers();
        List<Customer> results = new ArrayList<>();
        String lower = query.toLowerCase();
        for (Customer c : all) {
            if (c.getName().toLowerCase().contains(lower) ||
                    c.getPhone().contains(lower)) {
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
                VBox row = createSearchResultRow(c);
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

    private VBox createSearchResultRow(Customer c) {
        VBox row = new VBox(5);
        row.setStyle("-fx-padding: 10; -fx-cursor: hand;");
        row.setOnMouseEntered(e -> row.setStyle("-fx-padding: 10; -fx-cursor: hand; -fx-background-color: -color-neutral-subtle;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-padding: 10; -fx-cursor: hand;"));

        Label name = new Label(c.getName());
        name.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        HBox details = new HBox(10);
        Label phone = new Label(c.getPhone());
        phone.setStyle("-fx-font-size: 12px; -fx-text-fill: -color-fg-subtle;");

        String color = c.getStatus() == Status.ACTIVE ? "-color-success-emphasis" : "-color-danger-emphasis";
        Label status = new Label(c.getStatus().toString());
        status.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        details.getChildren().addAll(phone, status);
        row.getChildren().addAll(name, details);

        row.setOnMouseClicked(e -> {
            hideSearchResults();
            searchField.clear();
            openCustomerDialog(c);
        });
        return row;
    }

    // ===== Recent Customers (for DashboardHomeController) =====

    public void setRecentCustomers(List<Customer> customers) {
        recentCustomers.clear();
        if (customers != null) {
            recentCustomers.addAll(customers);
        }
    }

    public List<Customer> getRecentCustomers() {
        return new ArrayList<>(recentCustomers);
    }

    public void addRecentCustomer(Customer customer) {
        recentCustomers.removeIf(c -> c.getPhone().equals(customer.getPhone()));
        recentCustomers.addFirst(customer);
        while (recentCustomers.size() > 8) {
            recentCustomers.removeLast();
        }
    }

    // ===== Refresh after edits =====

    public void refreshAfterCustomerEdit() {
        // Reload whatever view is currently showing
        if (!contentArea.getChildren().isEmpty()) {
            Parent current = (Parent) contentArea.getChildren().get(0);
            // Check if it's MainWindow by looking for a known node
            // Simple approach: reload customer management if that's what's showing
            if (current.lookup("#customerTable") != null) {
                loadCustomerManagement();
            } else {
                loadDashboardHome();
            }
        }
    }

    public void refreshAfterGarmentEdit() {
        // Reload garment view if showing
        if (!contentArea.getChildren().isEmpty()) {
            Parent current = (Parent) contentArea.getChildren().get(0);
            if (current.lookup("#garmentTable") != null) {
                // Re-trigger garment view reload - but we need the customer context
                // This is handled by GarmentWindowController calling back
            }
        }
    }

    // ===== Logout =====

    @FXML
    private void handleLogout() {
        authService.logout();
        Stage stage = (Stage) contentArea.getScene().getWindow();
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
                Scene loginScene = new Scene(loginRoot, 400, 350);
                ThemeManager.apply(loginScene);
                loginStage.setScene(loginScene);
                loginStage.showAndWait();

                LoginController loginController = loginLoader.getController();
                if (loginController != null && loginController.isLoginSuccessful()) {
                    Platform.runLater(() -> {
                        Stage newDashboardStage = new Stage();
                        // Re-launch dashboard
                        // This is simplified; full re-launch logic in Launcher
                    });
                } else {
                    Platform.exit();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Platform.exit();
            }
        });
    }

    // ===== Getters for child controllers =====
    public CustomerService getCustomerService() { return customerService; }
    public GarmentService getGarmentService() { return garmentService; }
}