module com.gearrentpro {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.gearrentpro to javafx.fxml;
    exports com.gearrentpro;
}
