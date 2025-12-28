package com.gearrentpro;

import com.gearrentpro.util.DatabaseConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        // Test database connection
        if (!DatabaseConfig.testConnection()) {
            System.err.println("Failed to connect to database!");
            System.err.println("Please ensure MySQL is running and database is created.");
            System.exit(1);
        }
        
        // Load login screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        BorderPane root = loader.load();
        
        Scene scene = new Scene(root, 500, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("GearRent Pro - Login");
        primaryStage.setResizable(true);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
