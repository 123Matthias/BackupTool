package my.backup.backupTool.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import my.backup.backupTool.App;
import my.backup.backupTool.MessageTYPE;
import my.backup.backupTool.Model.*;
import my.backup.backupTool.Service.IUpdateScene;
import my.backup.backupTool.Service.MessageService;
import my.backup.backupTool.Service.SceneUpdateFXMLService;

import java.io.File;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.UnaryOperator;

public abstract class BaseDetailController {


    @FXML
    private StackPane stackPane;

    @FXML
    private TextField title;

    @FXML
    private Label header;

    @FXML
    private TextArea sourcePath;

    @FXML
    private Button sourceButton;

    @FXML
    private Button targetButton;

    @FXML
    private TextArea targetPath;
    @FXML
    private CheckBox checkBoxEncryption;

    @FXML
    private ComboBox<String> encryptionJobDropdown;

    @FXML
    private CheckBox checkBoxValidationJob;

    @FXML
    private ComboBox<String> validationJobDropdown;

    @FXML
    private ToolBar toolbar;

    private boolean isSourceButtonClicked = false;

    private boolean isTargetButtonClicked = false;

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
    private DatePicker startDateDatePicker;

    private BaseModel model;
    private IUpdateScene sceneUpdate;


    @FXML
    void initialize(){

        model = new MergeModel();
        initToolbar();

        // Button-Event-Handler für den Source-Button
        sourceButton.setOnAction(event -> {
            isSourceButtonClicked = true;
            openDirectoryChooser();
        });

        targetButton.setOnAction(event -> {
            isTargetButtonClicked = true;
            openDirectoryChooser();
        });

        sceneUpdate = new SceneUpdateFXMLService();

        encryptionJobDropdown.getSelectionModel().select(EncryptionTYPE.AES_CBC.toString());
        validationJobDropdown.getSelectionModel().select(ValidationTYPE.CRC32.toString());

        //Delegate filter nur zahlen
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();
            if (text.matches("[0-9]*")) {
                return change;
            }
            return null;
        };

        TextFormatter<String> formatter = new TextFormatter<>(filter);
        daysInterval.setTextFormatter(formatter);
        hoursInterval.setTextFormatter(new TextFormatter<>(filter));
    }

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
                    else if (node.getId().equals("pauseButton"))
                        node.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                            this.stopAndInterruptButtonClicked();
                            this.closeDetailAndReloadOverview();
                        });
                    else if (node.getId().equals("restoreButton"))
                        node.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                            if(!this.model.isRestoreMode()){
                                this.toggleSourceTargetPath();
                                this.enableRestoreMode();
                            }

                        });
                    else if (node.getId().equals("backupButton"))
                        node.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                            if(this.model.isRestoreMode()){
                                this.toggleSourceTargetPath();
                                this.enableBackupMode();
                            }

                        });
                    else if (node.getId().equals("deleteButton"))
                        node.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                            this.deleteBackup();
                            this.closeDetailAndReloadOverview();

                        });
                    else if (node.getId().equals("deleteSettingsButton"))
                        node.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                            this.deleteSettingsJSON();
                            this.closeDetailAndReloadOverview();
                        });
                    else if (node.getId().equals("playReverseButton"))
                        node.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                            this.playReverseButtonClicked();
                        });
                });
    }

    public StackPane getStackPane() {
        return stackPane;
    }

    public void setStackPane(StackPane stackPane) {
        this.stackPane = stackPane;
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
    protected void enableRestoreMode() {
        header.setText("Restore Target to Source");
        sourcePath.editableProperty().set(false);
        targetPath.editableProperty().set(false);
        sourceButton.setDisable(true);
        targetButton.setDisable(true);
        checkBoxIntervalDays.setDisable(true);
        checkBoxIntervalHours.setDisable(true);
        checkBoxStartDate.setDisable(true);
        changeRestoreModeColor(true);
        model.setRestoreMode(true);
    }

    @FXML
    protected void enableBackupMode() {
        header.setText("Backup");
        sourcePath.editableProperty().set(true);
        targetPath.editableProperty().set(true);
        sourceButton.setDisable(false);
        targetButton.setDisable(false);
        checkBoxIntervalDays.setDisable(false);
        checkBoxIntervalHours.setDisable(false);
        checkBoxStartDate.setDisable(false);
        changeRestoreModeColor(false);
        model.setRestoreMode(false);

    }

    private void toggleSourceTargetPath(){
        String source = model.getSource();
        String target = model.getTarget();
        model.setSource(target);
        model.setTarget(source);
        sourcePath.setText(model.getSource());
        targetPath.setText(model.getTarget());
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
    public void toggleEncryption(){
        boolean enable = checkBoxEncryption.isSelected();
        encryptionJobDropdown.setDisable(!enable);
    }

    @FXML
    public void toggleValidationType(){
        boolean enable = checkBoxValidationJob.isSelected();
        validationJobDropdown.setDisable(!enable);
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
        model.setRestoreMode(false);
        model.setBackupJob(true);
        this.setModelValues();
        if(model.validate()){
            App.DataStore.saveModelAsJSON(model);
            App.JobScheduler.fireBackupEvent(model);
            MessageService.createToast("Scheduling Backup");
            closeDetailAndReloadOverview();
            System.out.println("----------playButtonClickedDoneSuccessfully-----------");
        }
        else {
            MessageService.createMessage(model.getMessageList(), MessageTYPE.VALIDATION);
        }
    }

    @FXML
    private void playReverseButtonClicked(){
        model.setRestoreMode(true);
        model.setBackupJob(true);
        this.setModelValues();
        if(model.validate()){
            if(App.DataStore.saveModelAsJSON(model)){
                model.setSource(model.getTarget());
                model.setTarget(model.getSource());
                App.JobScheduler.fireBackupEvent(model);
                MessageService.createToast("Scheduling Backup");
                closeDetailAndReloadOverview();
                System.out.println("----------playButtonClickedDoneSuccessfully-----------");
            }

        }
        else {
            MessageService.createMessage(model.getMessageList(), MessageTYPE.VALIDATION);
        }
    }

    @FXML
    private void stopAndInterruptButtonClicked(){
        App.JobScheduler.stopAndInterruptBackupEvent(model);
        MessageService.createToast("stopped");
        closeDetailAndReloadOverview();
    }

    @FXML
    private void deleteSettingsJSON(){
        String uid = this.model.getUid();
        System.out.println("Settings to Delete: " + this.model.getUid());
        if(App.DataStore.deleteModelById_KeepBackup(uid)){
            MessageService.createToast("Entry Deleted");
        }
        else {
            MessageService.createToast("Something went wrong");
        }
    }

    @FXML
    private void deleteBackup(){
        String uid = this.model.getUid();
        if(App.DataStore.deleteModelAndBackupById(uid)){
            MessageService.createToast("Backup Deleted");
        }
        else {
            MessageService.createToast("Something went wrong");
        }
    }

    @FXML
    private void saveButtonClicked(){
        this.setModelValues();
        model.setBackupJob(false);
        if(model.validate()){
            if(App.DataStore.saveModelAsJSON(model)){
                MessageService.createToast("Saved Successfully");
            }
            closeDetailAndReloadOverview();

        }
        else {
            MessageService.createMessage(model.getMessageList(), MessageTYPE.VALIDATION);
        }
    }

    private void setModelValues(){
        if(checkBoxStartDate.isSelected() || checkBoxIntervalDays.isSelected() || checkBoxIntervalHours.isSelected()){
            LocalDateTime startDate;
            startDate = checkBoxStartDate.isSelected() && startDateDatePicker.getValue() != null ? startDateDatePicker.getValue().atTime(LocalTime.now())  : LocalDateTime.now();
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
            model.setNextBackupLocalDateTime(App.JobScheduler.calculateNextBackupTime(startDate, days, hours));
        }
        else{
            model.setNextBackupLocalDateTime(null);
        }

        model.setTitle(title.getText());

        model.setBackupType(BackupType.MERGE);
        model.setSource(sourcePath.getText());
        model.setTarget(targetPath.getText());

        /*TIMING*/
        model.setCheckBoxStartDate(checkBoxStartDate.isSelected());
        model.setCheckBoxDaysInterval(checkBoxIntervalDays.isSelected());
        model.setCheckBoxHoursInterval(checkBoxIntervalHours.isSelected());
        /*END TIMING*/

        /*Validation and Encryption*/
        model.setCheckBoxValidationJob(checkBoxValidationJob.isSelected());
        model.setCheckBoxEncryptionJob(checkBoxEncryption.isSelected());
        /*END Validation and Encryption*/

        if (checkBoxValidationJob.isSelected() && validationJobDropdown.getSelectionModel().getSelectedItem()
                .equals(ValidationTYPE.CRC32.toString())) {
            model.setValidationType(ValidationTYPE.CRC32);
            model.setValidationJob(true);
        }
        else{
            model.setValidationType(ValidationTYPE.NONE);
            model.setValidationJob(false);
        }

        if (checkBoxEncryption.isSelected() && encryptionJobDropdown.getSelectionModel().getSelectedItem()
                .equals(EncryptionTYPE.AES_CBC.toString())) {
            model.setEncryptionTYPE(EncryptionTYPE.AES_CBC);
            model.setEncryptionJob(true);
        }
        else{
            model.setEncryptionTYPE(EncryptionTYPE.NONE);
            model.setEncryptionJob(false);
        }

    }

    private void closeDetailAndReloadOverview(){
        sceneUpdate.reloadView("baseOverview.fxml");
        Stage stage = (Stage) stackPane.getScene().getWindow();
        stage.close();
    }

    public void openUpdateScene(BaseModel model){

        this.model = model;
        if(!model.isRestoreMode()){
            enableBackupMode();
        }
        else{
            enableRestoreMode();
        }


        title.setText(model.getTitle() != null ? model.getTitle() : "");
        sourcePath.setText(model.getSource() != null ? model.getSource() : "");
        targetPath.setText(model.getTarget() != null ? model.getTarget() : "");

        checkBoxStartDate.setSelected(model.getCheckBoxStartDate());
        startDateDatePicker.setValue(model.getStartDate() != null ? model.getStartDate().toLocalDate() : null);
        checkBoxIntervalDays.setSelected(model.getCheckBoxDaysInterval());
        daysInterval.setText(String.valueOf(model.getIntervalDays()));
        checkBoxIntervalHours.setSelected(model.getCheckBoxHoursInterval());
        hoursInterval.setText(String.valueOf(model.getIntervalHours()));

        checkBoxValidationJob.setSelected(model.getCheckBoxValidationJob());
        validationJobDropdown.getSelectionModel().select(   model.getValidationType() != null ?
                                                            model.getValidationType().toString() :
                                                            ValidationTYPE.CRC32.toString());

        checkBoxEncryption.setSelected(model.getCheckBoxEncryptionJob());
        encryptionJobDropdown.getSelectionModel().select(   model.getEncryptionType() != null ?
                                                            model.getEncryptionType().toString() :
                                                            EncryptionTYPE.AES_CBC.toString());

        this.toggleDate();
        this.toggleDays();
        this.toggleHours();


    }

}
