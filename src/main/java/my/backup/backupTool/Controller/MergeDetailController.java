package my.backup.backupTool.Controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import my.backup.backupTool.App;
import my.backup.backupTool.Model.BackupType;


public class MergeDetailController extends BaseDetailController {

    @Override
    @FXML
    public void initialize() {
        super.initialize();

    }

    @Override
    protected void setModelValues(){
        super.setModelValues();
        super.getModel().setBackupType(BackupType.MERGE);
    }

    @Override
    @FXML
    protected void playButtonClicked(){
        super.playButtonClicked();
    }

    @Override
    @FXML
    protected void saveButtonClicked() {
        super.saveButtonClicked();
        App.Router.getMainController().handleMergeButtonClicked();
        super.closeDetailAndReloadOverview();
    }
}
