package my.backup.backupTool.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import my.backup.backupTool.App;
import my.backup.backupTool.Model.MergeModel;
import my.backup.backupTool.Service.*;
import my.backup.backupTool.Model.IModel;
import my.backup.backupTool.DataRepository.BaseDataRepository;
import my.backup.backupTool.Theme;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class MergeDetailController {

    @FXML
    private StackPane stackPane;

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
    private Button backupButton;
    @FXML
    private Button restoreButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button playButton;
    @FXML
    private Button playReverseButton;
    @FXML
    private Button deleteSettingsButton;


    private boolean isSourceButtonClicked = false;
    private boolean isTargetButtonClicked = false;



    IMergeService mergeService;
    ITimeService timeService;
    ValidationService validationService;
    IModel model;
    BaseDataRepository dataStore;

    IUpdateScene sceneUpdate;




    @FXML
    public void initialize() {

        enableBackupMode();

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
        dataStore = new BaseDataRepository();
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
    private void enableBackupMode(){
        playButton.setVisible(true);
        playButton.setManaged(true);
        pauseButton.setVisible(true);
        pauseButton.setManaged(true);
        deleteButton.setVisible(true);
        deleteButton.setManaged(true);
        saveButton.setVisible(true);
        saveButton.setManaged(true);
        restoreButton.setVisible(true);
        restoreButton.setManaged(true);
        deleteSettingsButton.setVisible(true);
        deleteSettingsButton.setManaged(true);

        backupButton.setVisible(false);
        backupButton.setManaged(false);
        playReverseButton.setVisible(false);
        playReverseButton.setManaged(false);

        stackPane.getStyleClass().add("basic-background");

    }

    @FXML
    private void enableRestoreMode(){
        playButton.setVisible(false);
        playButton.setManaged(false);
        pauseButton.setVisible(false);
        pauseButton.setManaged(false);
        deleteButton.setVisible(false);
        deleteButton.setManaged(false);
        saveButton.setVisible(false);
        saveButton.setManaged(false);
        restoreButton.setVisible(false);
        restoreButton.setManaged(false);
        deleteSettingsButton.setVisible(false);
        deleteSettingsButton.setManaged(false);


        backupButton.setVisible(true);
        backupButton.setManaged(true);
        playReverseButton.setVisible(true);
        playReverseButton.setManaged(true);

        changeModeColor();

    }


    private void changeModeColor() {
        BackgroundFill backgroundFill;

        if (App.Router.getTheme() == Theme.DARK) {
            // Dunkler Rotton
            backgroundFill = new BackgroundFill(Color.rgb(50, 0, 0), null, null);  // Dunkler Rotton
        } else {
            // Heller Rotton
            backgroundFill = new BackgroundFill(Color.rgb(255, 215, 200), null, null);  // Hellerer Rotton
        }

        Background background = new Background(backgroundFill);
        stackPane.setBackground(background);
    }



    @FXML
    private void play(){
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
        saveData();

    }

    @FXML
    private void save(){
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
        model.setTitle(title.getText());
        model.setStartDate(startDate);
        model.setIntervalDays(days);
        model.setIntervalHours(hours);
        ValidationService.validatePath(new File(model.getSource()), new File(model.getTarget()));
        saveData();
        sceneUpdate.reloadView("mergeOverview.fxml");
    }




    public void openUpdateSceneByUID(String uid){

        try {
            model = dataStore.getModelById(uid);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        title.setText(model.getTitle() != null ? model.getTitle() : "");
        sourcePath.setText(model.getSource() != null ? model.getSource() : "");
        targetPath.setText(model.getTarget() != null ? model.getTarget() : "");
        if (model.getStartDate() != null) {
            datePicker.setValue(model.getStartDate().toLocalDate());
        }
        daysInterval.setText(String.valueOf(model.getIntervalDays()));
        hoursInterval.setText(String.valueOf(model.getIntervalHours()));

    }

    private boolean saveData(){
        return dataStore.saveModelAsJSON(model);
    }


}
