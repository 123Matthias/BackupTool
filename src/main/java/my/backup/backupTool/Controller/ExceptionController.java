package my.backup.backupTool.Controller;

import javafx.scene.control.Alert;


public class ExceptionController {

    public static void handleException(Exception e) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ups");
        alert.setHeaderText("An error occured");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}
