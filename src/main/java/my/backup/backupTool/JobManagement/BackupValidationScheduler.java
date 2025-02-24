package my.backup.backupTool.JobManagement;

import my.backup.backupTool.App;
import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.Enumerations.ValidationTYPE;
import my.backup.backupTool.Services.FileValidationService;
import my.backup.backupTool.Services.IFileValidationService;

import java.util.List;

public class BackupValidationScheduler {

    List<BaseModel> models;
    private static volatile BackupValidationScheduler Instance = null;


    private BackupValidationScheduler() {
        this.models = App.DataStore.getModelList();
    }

    public static BackupValidationScheduler Singleton() {
        synchronized (BackupValidationScheduler.class) {
            if (Instance == null) {
                Instance = new BackupValidationScheduler();
            }
        }
        return Instance;
    }


}
