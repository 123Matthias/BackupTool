package my.backup.backupTool.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
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

import java.io.File;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.UnaryOperator;


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
    private CheckBox checkBoxEncryption;

    @FXML
    private ComboBox encryptionDropdown;

    @FXML
    private CheckBox checkBoxCheckSum;

    @FXML
    private ComboBox checkSumDropdown;

    @FXML
    private DatePicker startDateDatePicker;

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
        sceneUpdate = new SceneUpdateFXMLService();


        //Delegate filter nur zahlen
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            if (text.matches("[0-9]*")) {
                return change;
            }
            return null;
        };

        // Anwendeung des "Delegate"
        TextFormatter<String> formatter = new TextFormatter<>(filter);
        daysInterval.setTextFormatter(formatter);
        hoursInterval.setTextFormatter(new TextFormatter<>(filter));
        encryptionDropdown.getSelectionModel().select("AES-CBC");
        checkSumDropdown.getSelectionModel().select("CRC32");
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
                            this.playButtonClicked();
                        });
                    else if (node.getId().equals("saveButton"))
                        node.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                            this.saveButtonClicked();
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
        startDateDatePicker.setDisable(!enable);
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

    @FXML
    public void toggleEncryption(){
        boolean enable = checkBoxEncryption.isSelected();
        encryptionDropdown.setDisable(!enable);
    }

    @FXML
    public void toggleCheckSum(){
        boolean enable = checkBoxCheckSum.isSelected();
        checkSumDropdown.setDisable(!enable);
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
    private void playButtonClicked(){
        this.setModelValues(true,true);
        if(model.validate()){
            App.DataStore.saveModelAsJSON(model);
            App.JobScheduler.fireBackupEvent(model);
            closeDetailAndReloadOverview();
            System.out.println("----------playButtonClickedDoneSuccessfully-----------");
        }
        else {
            MessageService.createMessage(model.getMessageList(), MessageTYPE.VALIDATION);
        }
    }

    @FXML
    private void pause(){
        model.setPlayBackupOrder(false);
        App.DataStore.saveModelAsJSON(model);
    }

    @FXML
    private void saveButtonClicked(){
        this.setModelValues(false,false);
        if(model.validate()){
            App.DataStore.saveModelAsJSON(model);
            closeDetailAndReloadOverview();
        }
        else {
            MessageService.createMessage(model.getMessageList(), MessageTYPE.VALIDATION);
        }
    }

    private void setModelValues(boolean backupOrder, boolean hashOrder){
        if(checkBoxStartDate.isSelected() || checkBoxIntervalDays.isSelected() || checkBoxIntervalHours.isSelected()){
            LocalDateTime startDate;
            startDate = checkBoxStartDate.isSelected() ? startDateDatePicker.getValue().atTime(LocalTime.now())  : LocalDateTime.now();
            model.setStartDate(startDate);
            int days = 0;
            int hours = 0;
            try{
                days = checkBoxIntervalDays.isSelected() ? Integer.parseInt(daysInterval.getText()):0;
                hours = checkBoxIntervalHours.isSelected() ? Integer.parseInt(hoursInterval.getText()):0;
                model.setIntervalDays(days);
                model.setIntervalHours(hours);
            }
            catch (NumberFormatException e){
                System.out.println("Not type of integer: " + e);
            }
            model.setNextBackupLocalDateTime(TimeService.calculateNextBackupTime(startDate, days, hours));
        }
        else{
            model.setNextBackupLocalDateTime(null);
        }

        model.setSource(sourcePath.getText());
        model.setTarget(targetPath.getText());
        model.setTitle(title.getText());
        model.setBackupType(BackupType.MERGE);
        model.setPlayBackupOrder(backupOrder);
        model.setHashOrder(hashOrder);
        model.setHashType(HashTYPE.CRC32);
    }

    private void closeDetailAndReloadOverview(){
        sceneUpdate.reloadView("baseOverview.fxml");
        MessageService.createToast("Saved Successfully");
        Stage stage = (Stage) stackPane.getScene().getWindow();
        stage.close();
    }


    public void openUpdateSceneByUID(String uid){

        model = App.DataStore.getModelById(uid);


        title.setText(model.getTitle() != null ? model.getTitle() : "");
        sourcePath.setText(model.getSource() != null ? model.getSource() : "");
        targetPath.setText(model.getTarget() != null ? model.getTarget() : "");


        checkBoxStartDate.setSelected(model.getStartDate() != null);
        LocalDateTime startDate = model.getStartDate();  // Dein LocalDateTime-Wert
        startDateDatePicker.setValue(startDate.toLocalDate());


        checkBoxIntervalDays.setSelected(model.getIntervalDays() != 0);
        daysInterval.setText(String.valueOf(model.getIntervalDays()));
        checkBoxIntervalHours.setSelected(model.getIntervalHours() != 0);
        hoursInterval.setText(String.valueOf(model.getIntervalHours()));

        this.toggleDate();
        this.toggleDays();
        this.toggleHours();

    }


}
