package my.backup.backupTool.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import my.backup.backupTool.Model.BackupType;

import java.util.function.UnaryOperator;


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
}
