package com.gearrentpro.controller;

import com.gearrentpro.entity.Rental;
import com.gearrentpro.entity.Reservation;
import com.gearrentpro.service.AuthenticationService;
import com.gearrentpro.service.RentalService;
import com.gearrentpro.service.ReservationService;
import java.sql.SQLException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ReservationListController {

  private List<Reservation> allReservations;
  private AuthenticationService authService;

  @FXML private TableColumn<Reservation, String> codeColumn;

  @FXML private TableColumn<Reservation, String> customerColumn;

  @FXML private TableColumn<Reservation, String> endDateColumn;

  @FXML private TableColumn<Reservation, String> equipmentColumn;

  private RentalService rentalService;
  private ReservationService reservationService;

  @FXML private TableView<Reservation> reservationTable;

  @FXML private TextField searchField;

  @FXML private TableColumn<Reservation, String> startDateColumn;

  @FXML private TableColumn<Reservation, String> statusColumn;

  @FXML
  private void handleCancelReservation() {
    Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
    if (selected == null) {
      showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select a reservation!");
      return;
    }

    Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
    confirmDialog.setTitle("Cancel Reservation");
    confirmDialog.setHeaderText("Are you sure?");
    confirmDialog.setContentText(
        "Cancel reservation: "
            + selected.getReservationCode()
            + "\nCustomer: "
            + selected.getCustomerName());

    if (confirmDialog.showAndWait().get() == ButtonType.OK) {
      try {
        // Update reservation status to CANCELLED
        boolean reservationCancelled =
            reservationService.updateReservationStatus(
                selected.getReservationId(), Reservation.ReservationStatus.CANCELLED);

        // Check if there is an associated rental and cancel it as well
        Rental rental = rentalService.getRentalByReservationId(selected.getReservationId());
        if (rental != null) {
          rentalService.updateRentalStatus(rental.getRentalId(), Rental.RentalStatus.CANCELLED);
        }

        if (reservationCancelled) {
          showAlert(Alert.AlertType.INFORMATION, "Success", "Booking cancelled successfully!");
          loadReservations();
        } else {
          showAlert(Alert.AlertType.ERROR, "Error", "Failed to cancel booking!");
        }
      } catch (SQLException e) {
        showAlert(
            Alert.AlertType.ERROR, "Database Error", "Failed to cancel booking: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  @FXML
  private void handleConvertToRental() {
    Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
    if (selected == null) {
      showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select a reservation!");
      return;
    }

    if (selected.getStatus() != Reservation.ReservationStatus.PENDING) {
      showAlert(
          Alert.AlertType.INFORMATION,
          "Info",
          "This reservation cannot be converted to a rental as it is not in 'PENDING' status.");
      return;
    }

    Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
    confirmDialog.setTitle("Convert to Rental");
    confirmDialog.setHeaderText("Are you sure?");
    confirmDialog.setContentText(
        "Convert reservation to rental: "
            + selected.getReservationCode()
            + "\nCustomer: "
            + selected.getCustomerName());

    if (confirmDialog.showAndWait().get() == ButtonType.OK) {
      try {
        if (reservationService.convertToRental(selected.getReservationId())) {
          showAlert(
              Alert.AlertType.INFORMATION, "Success", "Reservation converted to rental successfully!");
          loadReservations();
        } else {
          showAlert(Alert.AlertType.ERROR, "Error", "Failed to convert reservation to rental!");
        }
      } catch (Exception e) {
        showAlert(
            Alert.AlertType.ERROR,
            "Error",
            "Failed to convert reservation to rental: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  @FXML
  private void handleSearch() {
    String searchText = searchField.getText().toLowerCase().trim();

    if (searchText.isEmpty()) {
      loadReservations();
      return;
    }

    try {
      List<Reservation> filtered =
          allReservations.stream()
              .filter(
                  r -> {
                    boolean codeMatch =
                        r.getReservationCode() != null
                            && r.getReservationCode().toLowerCase().contains(searchText);
                    boolean customerMatch =
                        r.getCustomerName() != null
                            && r.getCustomerName().toLowerCase().contains(searchText);
                    boolean equipmentMatch =
                        r.getEquipmentDetails() != null
                            && r.getEquipmentDetails().toLowerCase().contains(searchText);
                    return codeMatch || customerMatch || equipmentMatch;
                  })
              .collect(java.util.stream.Collectors.toList());

      if (filtered.isEmpty()) {
        showAlert(
            Alert.AlertType.INFORMATION, "Info", "No reservations found matching: " + searchText);
      }

      ObservableList<Reservation> observableList = FXCollections.observableArrayList(filtered);
      reservationTable.setItems(observableList);

    } catch (Exception e) {
      showAlert(Alert.AlertType.ERROR, "Error", "Search failed: " + e.getMessage());
    }
  }

  @FXML
  public void initialize() {
    reservationService = ReservationService.getInstance();
    rentalService = RentalService.getInstance();
    authService = AuthenticationService.getInstance();
    setupTableColumns();
    loadReservations();
  }

  private void loadReservations() {
    try {
      Integer branchId = authService.getCurrentUserBranchId();
      if (branchId != null) {
        // Branch user, load reservations for the specific branch
        allReservations = reservationService.getReservationsByBranch(branchId);
      } else {
        // Admin user, load all reservations
        allReservations = reservationService.getAllReservations();
      }

      if (allReservations == null || allReservations.isEmpty()) {
        showAlert(Alert.AlertType.INFORMATION, "Info", "No reservations found!");
        reservationTable.setItems(FXCollections.observableArrayList());
        return;
      }

      // Display in table
      ObservableList<Reservation> observableList = FXCollections.observableArrayList(allReservations);
      reservationTable.setItems(observableList);

      System.out.println("Loaded " + allReservations.size() + " reservations");

    } catch (SQLException e) {
      showAlert(
          Alert.AlertType.ERROR, "Database Error", "Failed to load reservations: " + e.getMessage());
      e.printStackTrace();
    } catch (Exception e) {
      showAlert(Alert.AlertType.ERROR, "Error", "Unexpected error: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void setupTableColumns() {
    codeColumn.setCellValueFactory(
        cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getReservationCode() != null
                    ? cellData.getValue().getReservationCode()
                    : "N/A"));

    customerColumn.setCellValueFactory(
        cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getCustomerName() != null
                    ? cellData.getValue().getCustomerName()
                    : "N/A"));

    equipmentColumn.setCellValueFactory(
        cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getEquipmentDetails() != null
                    ? cellData.getValue().getEquipmentDetails()
                    : "N/A"));

    startDateColumn.setCellValueFactory(
        cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getStartDate() != null
                    ? cellData.getValue().getStartDate().toString()
                    : "N/A"));

    endDateColumn.setCellValueFactory(
        cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getEndDate() != null
                    ? cellData.getValue().getEndDate().toString()
                    : "N/A"));

    statusColumn.setCellValueFactory(
        cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getStatus() != null
                    ? cellData.getValue().getStatus().toString()
                    : "N/A"));
  }

  private void showAlert(Alert.AlertType type, String title, String message) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}