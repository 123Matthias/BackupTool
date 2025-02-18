package my.backup.backupTool.Controller.Merge;

import javafx.fxml.FXML;
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
        App.Router.getMainController().getMergeHelperController().handleMergeButtonClicked();
        super.closeDetailAndReloadOverview();
    }
}
