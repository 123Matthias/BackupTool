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
import my.backup.backupTool.DataRepository.IStoreData;
import my.backup.backupTool.DataRepository.BaseDataRepository;
import java.util.UUID;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SceneMergeDetailController {

    @FXML
    private TextField title;

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
    IModel model;
    IStoreData setting;
    IUpdateScene sceneUpdate;




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
        setting = new BaseDataRepository();
        mergeService = new MergeService();
        timeService = new TimeService();
        sceneUpdate = new SceneUpdateFXMLService();
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
        model.setSource(sourcePath.getText());
        model.setTarget(targetPath.getText());
        System.out.println("Meine Model Daten: " + "Source" +  model.getSource() + "Target" + model.getTarget());
        mergeService.startMergeData(model);
        saveSettings();

    }

    @FXML
    private void saveMergeBackup(){
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

        //TODO UUID handling für update
        if(model.getUid() == null || model.getUid().isEmpty()){
            model.setUid(UUID.randomUUID().toString());
        }

        timeService.setTiming(startDate, days, hours);
        model.setSource(sourcePath.getText());
        model.setTarget(targetPath.getText());
        model.setTitle(title.getText());
        model.setStartDate(startDate);
        model.setIntervalDays(days);
        model.setIntervalHours(hours);
        saveSettings();
        sceneUpdate.reloadView("mergeOverview.fxml");
    }

    public void updateUID(String uid){
        BaseDataRepository repo = new BaseDataRepository();
        IModel model = null;
        try {
            model = repo.getModelById(uid);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        model.getUid();
        title.setText(model.getTitle() != null ? model.getTitle() : "");
        sourcePath.setText(model.getSource() != null ? model.getSource() : "");
        targetPath.setText(model.getTarget() != null ? model.getTarget() : "");



    }

    private boolean saveSettings(){
        return setting.saveModelAsJSON(model);
    }


}
