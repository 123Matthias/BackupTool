package my.backup.backupTool.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import my.backup.backupTool.Main;
import my.backup.backupTool.Model.MergeModel;
import my.backup.backupTool.Service.*;
import my.backup.backupTool.Model.IModel;
import my.backup.backupTool.Data.IStoreData;
import my.backup.backupTool.Data.MergeData;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class MergeDetailController {

    @FXML
    private CheckBox checkBoxPathing;

    @FXML
    private CheckBox checkBoxStartDate;

    @FXML
    private CheckBox checkBoxIntervalDays;

    @FXML
    private CheckBox checkBoxIntervalHours;

    @FXML
    private TextField daysInterval;

    @FXML
    private TextField hoursInterval;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextArea sourcePath;

    @FXML
    private Button sourceButton;

    @FXML
    private Button targetButton;

    @FXML
    private TextArea targetPath;

    @FXML
    private Button playButton;

    private boolean isSourceButtonClicked = false;
    private boolean isTargetButtonClicked = false;

    IMergeService mergeService;
    ITimeService timeService;
    IPathService pathService;
    IModel model;
    IStoreData setting;




    @FXML
    public void initialize() {

        // Button-Event-Handler für den Source-Button
        sourceButton.setOnAction(event -> {
            isSourceButtonClicked = true;
            openDirectoryChooser();
        });

        targetButton.setOnAction(event -> {
            isTargetButtonClicked = true;
            openDirectoryChooser();
        });
        // MVC Model Initialisierung für MergeController;
        model = new MergeModel();
        setting = new MergeData();
        mergeService = new MergeService();
        timeService = new TimeService();
        pathService = new PathService();
    }
    @FXML
    public void toggleTextAreas() {
        boolean enable = checkBoxPathing.isSelected();
        sourcePath.setDisable(!enable);
        targetPath.setDisable(!enable);
        sourceButton.setDisable(!enable);
        targetButton.setDisable(!enable);
    }

    @FXML
    public void toggleDate() {
        boolean enable = checkBoxStartDate.isSelected();
        datePicker.setDisable(!enable);
    }

    @FXML
    public void toggleDays() {
        boolean enable = checkBoxIntervalDays.isSelected();
        daysInterval.setDisable(!enable);
    }

    @FXML
    public void toggleHours() {
        boolean enable = checkBoxIntervalHours.isSelected();
        hoursInterval.setDisable(!enable);
    }

    public void openDirectoryChooser() {
        // Erstelle ein FileChooser-Objekt
        DirectoryChooser directoryChooser = new DirectoryChooser();

        directoryChooser.setTitle("Select Folder");

        // Zeige den FileChooser an und erhalte die ausgewählte Datei
        File selectedFile = directoryChooser.showDialog(sourcePath.getScene().getWindow());

        // Wenn eine Datei ausgewählt wurde, setze den Pfad in das TextArea
        if (selectedFile != null && isSourceButtonClicked) {
            sourcePath.setText(selectedFile.getAbsolutePath());
            isSourceButtonClicked = false;
        }

        else if (selectedFile != null && isTargetButtonClicked) {
            targetPath.setText(selectedFile.getAbsolutePath());
            isTargetButtonClicked = false;
        }
    }

    @FXML
    private void startMergeBackup(){
        System.out.println("Source: " + sourcePath.getText());
        System.out.println("Target" + targetPath.getText());
        int days = 0;
        int hours = 0;
        try{
            days = Integer.parseInt(daysInterval.getText());
            hours = Integer.parseInt(hoursInterval.getText());
        }
        catch (NumberFormatException e){
            System.out.println("Not type of integer: " + e);
        }

        LocalDateTime startDate = LocalDateTime.now();
        startDate = (datePicker != null && datePicker.getValue() != null)
                ? datePicker.getValue().atTime(LocalTime.now())
                : startDate;


        timeService.setTiming(startDate, days, hours);
        model = pathService.setModelPath(sourcePath.getText(), targetPath.getText(), model);
        System.out.println("Meine Model Daten: " + "Source" +  model.getSource() + "Target" + model.getTarget());
        mergeService.startMergeData(model);
       saveSettings();

    }

    private boolean saveSettings(){
        return setting.saveModelAsJSON(model);
    }


    @FXML
    private void handleBackButton() throws IOException {

        FXMLLoader fxmlMergeBackup = new FXMLLoader(Main.class.getResource("mergeDetail.fxml"));
        Scene scene = new Scene(fxmlMergeBackup.load(), 800, 800);
        Main.mainStage.setTitle("Merge");
        Main.mainStage.setMinWidth(800);
        Main.mainStage.setMinHeight(800);
        scene.getStylesheets().add(String.valueOf(getClass().getResource("css/main.css")));
        Main.mainStage.setScene(scene);
        Main.mainStage.show();
    }


}
