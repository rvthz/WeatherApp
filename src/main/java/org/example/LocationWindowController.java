package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.util.List;

public class LocationWindowController {

    @FXML
    private ComboBox<LocationResult> locationComboBox;

    @FXML
    private Button selectButton;

    private List<LocationResult> locationResults;

    public void setLocationResults(List<LocationResult> locationResults) {
        this.locationResults = locationResults;
        locationComboBox.getItems().addAll(locationResults);
    }

//    @FXML
//    private void handleSelectLocation() {
//        LocationResult selectedLocation = locationComboBox.getValue();
//        if (selectedLocation != null) {
//            PrimaryController primaryController = new PrimaryController();
//            primaryController.searchButton.setDisable(true);
//            primaryController.latitudeField.setText(String.valueOf(selectedLocation.getLatitude()));
//            primaryController.longitudeField.setText(String.valueOf(selectedLocation.getLongitude()));
//            primaryController.searchButton.fire();
//            Stage stage = (Stage) selectButton.getScene().getWindow();
//            stage.close();
//        }
//    }
}
