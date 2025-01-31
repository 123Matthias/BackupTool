package my.backup.backupTool.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import my.backup.backupTool.App;
import my.backup.backupTool.MessageTYPE;
import my.backup.backupTool.Model.BackupType;
import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.Model.HashTYPE;
import my.backup.backupTool.Model.MergeModel;
import my.backup.backupTool.Service.*;
import my.backup.backupTool.DataRepository.BaseDataStoreRepository;

import java.io.File;
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
    private Label header;

    @FXML
    private ToolBar toolbar;

    private boolean isSourceButtonClicked = false;
    private boolean isTargetButtonClicked = false;


    ITimeService timeService;
    BaseModel model;
    IUpdateScene sceneUpdate;


    @FXML
    public void initialize() {
        initToolbar();
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
        timeService = new TimeService();
        sceneUpdate = new SceneUpdateFXMLService();
    }

    public StackPane getStackPane() {
        return stackPane;
    }

    public void setStackPane(StackPane stackPane) {
        this.stackPane = stackPane;
    }

    /*Toolbar Toggler FXML Toolbar is reusable and includet in Parent FXML file*/
    //TODO REUSABLE CONTROLLER with Annotaion @FXML if possible

    private void initToolbar(){
        toolbar.getItems().stream()
                .filter(node -> node.getId() != null).forEach(node -> {
                    if (node.getId().equals("playButton"))
                        node.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                            this.play();
                        });
                    else if (node.getId().equals("saveButton"))
                        node.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                            this.save();
                        });
                    else if (node.getId().equals("restoreButton"))
                        node.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                            this.enableRestoreMode();
                        });
                    else if (node.getId().equals("backupButton"))
                        node.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                            this.enableBackupMode();
                        });
                });
    }

    private void enableRestoreMode() {
        header.setText("Restore Target to Source");
        sourcePath.editableProperty().set(false);
        targetPath.editableProperty().set(false);
        sourceButton.setDisable(true);
        targetButton.setDisable(true);
        checkBoxIntervalDays.setDisable(true);
        checkBoxIntervalHours.setDisable(true);
        checkBoxStartDate.setDisable(true);
        changeRestoreModeColor(true);
    }

    private void enableBackupMode() {
        header.setText("Backup");
        sourcePath.editableProperty().set(true);
        targetPath.editableProperty().set(true);
        sourceButton.setDisable(false);
        targetButton.setDisable(false);
        checkBoxIntervalDays.setDisable(false);
        checkBoxIntervalHours.setDisable(false);
        checkBoxStartDate.setDisable(false);
        changeRestoreModeColor(false);
}


    private void changeRestoreModeColor(boolean restoreMode) {


        stackPane.getStyleClass().removeAll("danger", "basicBackground");

        if (restoreMode) {
            stackPane.getStyleClass().add("danger");
        } else {
            stackPane.getStyleClass().add("basicBackground");
        }
    }



/*
    private void enableAllToolbarButtons(){
        toolbar.getItems().stream()
                .filter(node -> node.getId() != null && node.getId().contains("Button"))
                .forEach(node -> {node.setVisible(true); node.setManaged(true);});
    }

    private void disableAllToolbarButtons(){
        toolbar.getItems().stream()
                .filter(node -> node.getId() != null && node.getId().contains("Button"))
                .forEach(node -> {node.setVisible(false); node.setManaged(false);});
    }


    @FXML
    private void enableBackupMode(){
        disableAllToolbarButtons();
        toolbar.getItems().stream()
                .filter(node -> node.getId() != null && node.getId().contains("Button")
                        && !node.getId().equals("backupButton")
                        && !node.getId().equals("playReverseButton"))
                .forEach(node -> {node.setVisible(true); node.setManaged(true);});
        header.setText("Backup Mode");
        changeModeColor();
    }


    @FXML
    private void enableRestoreMode() {
        enableAllToolbarButtons();
        toolbar.getItems().stream()
                .filter(node -> node.getId() != null && node.getId().contains("Button")
                        && !node.getId().equals("backupButton")
                        && !node.getId().equals("playReverseButton"))
                .forEach(node -> {node.setVisible(false); node.setManaged(false);});
        header.setText("Restore Mode");
        changeModeColor();
    }

*/

    /*----------------------END------------------------------------*/



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
    private void play(){
        System.out.println("---------METHOD PLAY STARTED -------------------------------");
        model.setPlayBackupOrder(true);
        model.setHashOrder(true);
        model.setHashType(HashTYPE.CRC32);
        save();
        saveData();

        System.out.println("---------METHOD PLAY FINISHED -------------------------------");

    }

    @FXML
    private void pause(){
        System.out.println("---------METHOD Pause STARTED -------------------------------");
        model.setPlayBackupOrder(false);
        saveData();
        System.out.println("---------METHOD Pause FINISHED -------------------------------");

    }

    @FXML
    private boolean save(){
     //   System.out.println("Source: " + sourcePath.getText());
     //   System.out.println("Target: " + targetPath.getText());
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
        model.setBackupType(BackupType.MERGE);

        if(model.validate()){
            saveData();
            System.out.println("----------------METHOD SAVED ------------------------------");
            sceneUpdate.reloadView("mergeOverview.fxml");
            MessageService.createToast("Saved Successfully");
            Stage stage = (Stage) stackPane.getScene().getWindow();
            stage.close();
            return true;
        }
        else {
            MessageService.createMessage(model.getMessageList(), MessageTYPE.VALIDATION);
            return false;
        }
    }


    public void openUpdateSceneByUID(String uid){

        model = App.DataStore.getModelById(uid);


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
        return App.DataStore.saveModelAsJSON(model);
    }

}
