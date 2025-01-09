package my.backup.backuptool.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import my.backup.backuptool.MainApplication;

import java.io.File;
import java.io.IOException;

public class MergeController {



    @FXML
    private CheckBox checkBoxPathing;

    @FXML
    private TextArea sourcePath;

    @FXML
    private TextArea targetPath;

    @FXML
    public void toggleTextAreas() {
        boolean enable = checkBoxPathing.isSelected();
        sourcePath.setDisable(!enable);
        targetPath.setDisable(!enable);
    }

    // Methode zum Öffnen des FileChoosers
    @FXML
    public void openDirectoryChooser() {
        // Erstelle ein FileChooser-Objekt
        DirectoryChooser directoryChooser = new DirectoryChooser();

        directoryChooser.setTitle("Select Folder");

        // Zeige den FileChooser an und erhalte die ausgewählte Datei
        File selectedFile = directoryChooser.showDialog(sourcePath.getScene().getWindow());

        // Wenn eine Datei ausgewählt wurde, setze den Pfad in das TextArea
        if (selectedFile != null) {
            sourcePath.setText(selectedFile.getAbsolutePath());
        }
    }


    @FXML
    private void handleBackButton() throws IOException {

        FXMLLoader fxmlMergeBackup = new FXMLLoader(MainApplication.class.getResource("merge.fxml"));
        Scene scene = new Scene(fxmlMergeBackup.load(), 800, 800);
        MainApplication.mainStage.setTitle("Merge");
        MainApplication.mainStage.setMinWidth(800);
        MainApplication.mainStage.setMinHeight(800);
        scene.getStylesheets().add(String.valueOf(getClass().getResource("css/main.css")));
        MainApplication.mainStage.setScene(scene);
        MainApplication.mainStage.show();
    }
}
