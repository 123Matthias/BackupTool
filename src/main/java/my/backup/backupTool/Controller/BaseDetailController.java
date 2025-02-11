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
import my.backup.backupTool.Encryption.EncryptionTYPE;
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
    private CheckBox checkBoxEncryptionJob;

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
    private CheckBox checkBoxIntervalMinutes;

    @FXML
    private TextField daysInterval;

    @FXML
    private TextField hoursInterval;

    @FXML
    private TextField minutesInterval;

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

    public BaseModel getModel() {
        return model;
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
    public void toggleMinutes() {
        boolean enable = checkBoxIntervalMinutes.isSelected();
        minutesInterval.setDisable(!enable);
    }

    @FXML
    protected void enableRestoreMode() {
        header.setText("Restore Target to Source");
        sourcePath.editableProperty().set(false);
        targetPath.editableProperty().set(false);
        sourceButton.setDisable(true);
        targetButton.setDisable(true);
        checkBoxIntervalDays.setDisable(true);
        checkBoxIntervalDays.setSelected(false);
        daysInterval.setDisable(true);
        checkBoxIntervalHours.setDisable(true);
        checkBoxIntervalHours.setSelected(false);
        hoursInterval.setDisable(true);
        checkBoxStartDate.setDisable(true);
        checkBoxStartDate.setSelected(false);
        startDateDatePicker.setDisable(true);
        checkBoxEncryptionJob.setDisable(true);
        checkBoxValidationJob.setDisable(true);
        checkBoxValidationJob.setSelected(model.getCheckBoxValidationJob());
        checkBoxEncryptionJob.setSelected(model.getCheckBoxEncryptionJob());
        validationJobDropdown.getSelectionModel().select(model.getValidationType().toString());
        encryptionJobDropdown.getSelectionModel().select(model.getEncryptionType().toString());
        checkBoxStartDate.setSelected(false);
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
        checkBoxEncryptionJob.setDisable(false);
        checkBoxValidationJob.setDisable(false);
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
    public void toggleEncryptionJob(){
        boolean enable = checkBoxEncryptionJob.isSelected();
        encryptionJobDropdown.setDisable(!enable);
    }

    @FXML
    public void toggleValidationJob(){
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
            MessageService.createToast("PLAY", MessageTYPE.PLAY);
            closeDetailAndReloadOverview();
            System.out.println("----------playButtonClickedDoneSuccessfully-----------");
        }
        else {
            MessageService.createMessage(model.TransientProperties.getMessageList(), MessageTYPE.VALIDATION);
        }
    }

    @FXML
    private void playReverseButtonClicked(){
        model.setRestoreMode(true);
        model.setBackupJob(true);
        this.setModelValues();
        if(model.validate()){
            if(App.DataStore.saveModelAsJSON(model)){
                App.JobScheduler.fireBackupEvent(model);
                MessageService.createToast("RESTORE BACKUP", MessageTYPE.PLAY);
                closeDetailAndReloadOverview();
                System.out.println("----------playButtonClickedDoneSuccessfully-----------");
            }

        }
        else {
            MessageService.createMessage(model.TransientProperties.getMessageList(), MessageTYPE.VALIDATION);
        }
    }

    @FXML
    private void stopAndInterruptButtonClicked(){
        App.JobScheduler.stopAndInterruptBackupEvent(model);
        MessageService.createToast("stopped", MessageTYPE.STOP);
        closeDetailAndReloadOverview();
    }

    @FXML
    private void deleteSettingsJSON(){
        String uid = this.model.getUid();
        System.out.println("Settings to Delete: " + this.model.getUid());
        if(App.DataStore.deleteModelById_KeepBackup(uid)){
            MessageService.createToast("Entry Deleted", MessageTYPE.DANGER);
        }
        else {
            //TODO Result exception etc
            System.err.println("Something went wrong");
        }
    }

    @FXML
    private void deleteBackup(){
        String uid = this.model.getUid();
        if(App.DataStore.deleteModelAndBackupById(uid)){
            MessageService.createToast("Backup Deleted", MessageTYPE.DANGER);
        }
        else {
            //TODO Result exception etc
            System.err.println("Something went wrong");
        }
    }

    @FXML
    private void saveButtonClicked(){
        this.setModelValues();
        model.setBackupJob(false);
        if(model.validate()){
            if(App.DataStore.saveModelAsJSON(model)){
                MessageService.createToast("Saved", MessageTYPE.SAVE);
            }
            closeDetailAndReloadOverview();

        }
        else {
            MessageService.createMessage(model.TransientProperties.getMessageList(), MessageTYPE.VALIDATION);
        }
    }

    protected void setModelValues(){

        if(model.isRestoreMode()){
            model.setStartDate(null);
            model.setIntervalDays(0);
            model.setIntervalHours(0);
            model.setIntervalMinutes(0);
            model.setNextBackupLocalDateTime(null);
        }

        else if(checkBoxStartDate.isSelected() || checkBoxIntervalDays.isSelected() || checkBoxIntervalHours.isSelected()){
            LocalDateTime startDate;
            startDate = checkBoxStartDate.isSelected() && startDateDatePicker.getValue() != null ?
                                                                                startDateDatePicker.getValue().atTime(LocalTime.now()) :
                                                                                LocalDateTime.now();
            model.setStartDate(startDate);
            int days = 0;
            int hours = 0;
            int minutes = 0;
            try{
                days = checkBoxIntervalDays.isSelected() ? Integer.parseInt(daysInterval.getText()):0;
                hours = checkBoxIntervalHours.isSelected() ? Integer.parseInt(hoursInterval.getText()):0;
                minutes = checkBoxIntervalMinutes.isSelected() ? Integer.parseInt(minutesInterval.getText()):0;
                model.setIntervalDays(days);
                model.setIntervalHours(hours);
                model.setIntervalMinutes(minutes);
            }
            catch (NumberFormatException e){
                System.out.println("Not type of integer: " + e);
            }
            model.setNextBackupLocalDateTime(App.JobScheduler.calculateNextBackupTime(model));
        }
        else {
            model.setNextBackupLocalDateTime(null);
        }

        model.setTitle(title.getText());


        model.setSource(sourcePath.getText());
        model.setTarget(targetPath.getText());

        /*TIMING*/
        model.setCheckBoxStartDate(checkBoxStartDate.isSelected());
        model.setCheckBoxDaysInterval(checkBoxIntervalDays.isSelected());
        model.setCheckBoxHoursInterval(checkBoxIntervalHours.isSelected());
        model.setCheckBoxMinutesInterval(checkBoxIntervalMinutes.isSelected());

        /*END TIMING*/

        /*Validation and Encryption*/
        model.setCheckBoxValidationJob(checkBoxValidationJob.isSelected());
        model.setCheckBoxEncryptionJob(checkBoxEncryptionJob.isSelected());
        /*END Validation and Encryption*/

        if (checkBoxValidationJob.isSelected()) {
            String selectedValueString = validationJobDropdown.getSelectionModel().getSelectedItem();
            model.setValidationType(selectedValueString.equals(ValidationTYPE.CRC32.toString()) ? ValidationTYPE.CRC32 : ValidationTYPE.NONE);
            model.setValidationJob(true);
        }
        else{
            model.setValidationType(ValidationTYPE.NONE);
            model.setValidationJob(false);
        }

        if (checkBoxEncryptionJob.isSelected()) {
             String selectedValueString = encryptionJobDropdown.getSelectionModel().getSelectedItem();
            model.setEncryptionType(selectedValueString.equals(EncryptionTYPE.AES_CBC.toString()) ? EncryptionTYPE.AES_CBC : EncryptionTYPE.NONE);
            model.setEncryptionJob(true);
        }
        else{
            model.setEncryptionType(EncryptionTYPE.NONE);
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
        checkBoxIntervalMinutes.setSelected(model.getCheckBoxMinutesInterval());
        minutesInterval.setText(String.valueOf(model.getIntervalMinutes()));

        checkBoxValidationJob.setSelected(model.getCheckBoxValidationJob());
        validationJobDropdown.getSelectionModel().select(   model.getValidationType() != null ?
                                                            model.getValidationType().toString() :
                                                            ValidationTYPE.CRC32.toString());

        checkBoxEncryptionJob.setSelected(model.getCheckBoxEncryptionJob());
        encryptionJobDropdown.getSelectionModel().select(   model.getEncryptionType() != null ?
                                                            model.getEncryptionType().toString() :
                                                            EncryptionTYPE.AES_CBC.toString());

        this.toggleDate();
        this.toggleDays();
        this.toggleHours();
        this.toggleEncryptionJob();
        this.toggleValidationJob();


    }

}
