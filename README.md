# GearRent Pro - Multi-Branch Equipment Rental System

## Project Description

GearRent Pro is a comprehensive, multi-branch equipment rental system designed to manage the business activities of a professional equipment rental company. The system supports a wide range of features, including inventory management, customer relationship management, reservations, and rentals. It is built using JavaFX for the user interface, Maven for dependency management, and MySQL for the database.

## Database Setup

1.  **Create the Database:**
    *   Open your MySQL client (e.g., MySQL Workbench, DBeaver) and run the following command to create the database:
        ```sql
        CREATE DATABASE gearrent_pro;
        ```
2.  **Run the Schema Script:**
    *   The `db_schema.sql` file, located in the `src/database` directory, contains the complete database schema, including tables, relationships, and sample data.
    *   Execute this script to create the necessary tables and populate them with initial data.

## Application Execution

1.  **Configure Database Credentials:**
    *   Before running the application, you need to configure the database credentials.
    *   In the `src/main/resources` directory, you will find a file named `config.properties.example`.
    *   Make a copy of this file and rename it to `config.properties`.
    *   Open the `config.properties` file and replace the placeholder values with your MySQL username and password.
2. **JavaFX Configuration:**
    *   First download this `https://download2.gluonhq.com/openjfx/21.0.9/openjfx-21.0.9_windows-x64_bin-sdk.zip` and unzip
    *   Then add .vscode folder to project and create lunch.json file and paste this.
        ```
        {
            "version": "0.2.0",
            "configurations": [
               {
                  "type": "java",
                  "name": "Run JavaFX App",
                  "request": "launch",
                  "mainClass": "com.gearrent.Main",
                  "vmArgs": "--module-path D:/javafx/javafx-sdk-21.0.9/lib --add-modules javafx.controls,javafx.fxml"
               }
            ]
        }
        ```
    * After that change this `D:/javafx/javafx-sdk-21.0.9/lib` according to your JavaFX jdk download location.
3.  **Run the Application:**
    *   Know You can run the application directly from your IDE or by using the following Maven command in your terminal:
        ```bash
        mvn javafx:run
        ```

## Default Login Credentials

The `db_schema.sql` script includes a set of default users with different roles. You can use these credentials to log in and test the application:

*   **Admin:**
    *   **Username:** admin
    *   **Password:** admin123
*   **Branch Manager (Panadura Branch):**
    *   **Username:** manager1
    *   **Password:** manager123
*   **Branch Manager (Galle Branch):**
    *   **Username:** manager2
    *   **Password:** manager123
*   **Staff (Panadura Branch):**
    *   **Username:** staff1
    *   **Password:** staff123
*   **Staff (Galle Branch):**
    *   **Username:** staff2
    *   **Password:** staff123
