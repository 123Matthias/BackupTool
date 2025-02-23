package my.backup.backupTool.Controller.Merge;

import javafx.fxml.FXML;
import my.backup.backupTool.App;
import my.backup.backupTool.Enumerations.BackupTYPE;


public class MergeDetailController extends BaseDetailController {

    @Override
    @FXML
    public void initialize() {
        super.initialize();

    }

    @Override
    protected void setModelValues(){
        super.setModelValues();
        super.getModel().setBackupType(BackupTYPE.MERGE);
    }

    @Override
    @FXML
    protected void playButtonClicked() {
        super.playButtonClicked();
        App.Router.getMainController().getMergeHelperController().handleMergeOverviewButtonClicked();
        super.closeDetailAndReloadOverview();
    }

    @Override
    @FXML
    protected void saveButtonClicked() {
        super.saveButtonClicked();
        App.Router.getMainController().getMergeHelperController().handleMergeOverviewButtonClicked();
        super.closeDetailAndReloadOverview();
    }
}
