package my.backup.backupTool.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

public class ExceptionController {

    @FXML
    private Label exceptionLabel; // Das Label für Fehlermeldungen

    // Diese Methode wird aufgerufen, wenn eine Exception übergeben wird
    public void showError(String errorMessage) {
        // Zeigt die Fehlermeldung im Label an
        exceptionLabel.setText("Fehler: " + errorMessage);

        // Optional: Zeigt zusätzlich ein Alert-Dialog mit der Fehlermeldung an
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehler");
        alert.setHeaderText("Ein Fehler ist aufgetreten");
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }
}
