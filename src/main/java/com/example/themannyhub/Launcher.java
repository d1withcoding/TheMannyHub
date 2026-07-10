package com.example.themannyhub;

import com.example.themannyhub.controllers.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**

 */
public class Launcher extends Application {


    public static void main(String[] args) {
        Application.launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the main window from FXML file
        // The path starts from the resources folder
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/themannyhub/MainWindow.fxml")
        );

        // Create the scene (the content inside the window)
        // You can adjust the width and height to fit your screen
        Scene scene = new Scene(loader.load(), 1100, 600);

        // Set the window title (appears in the title bar)
        primaryStage.setTitle("The Manny Hub - Customer Manager");

        // Put the scene in the window
        primaryStage.setScene(scene);

        // Optional: Set minimum window size so it can't get too small
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(400);

        // Show the window to the user
        primaryStage.show();
    }
}