package my.backup.backupTool.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import my.backup.backupTool.App;
import my.backup.backupTool.Model.BaseModel;

public class ToolbarDetailController {


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

    private BaseModel model;

    @FXML
    void initialize() {
        this.model = App.DataStore.getLastSelectedModel();
        if(model == null) {
            return;
        }
        if(model.isRestoreMode()){
            enableRestoreMode();
        }
        else{
            enableBackupMode();
        }
    }

    @FXML
    protected void enableBackupMode() {
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

    }

    @FXML
    protected void enableRestoreMode() {
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


    }
}
