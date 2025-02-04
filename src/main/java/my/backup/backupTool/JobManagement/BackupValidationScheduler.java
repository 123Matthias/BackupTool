package my.backup.backupTool.JobManagement;

import my.backup.backupTool.App;
import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.Model.ValidationTYPE;
import my.backup.backupTool.Service.FileValidationService;
import my.backup.backupTool.Service.IFileValidationService;

import java.util.List;

public class BackupValidationScheduler {

    List<BaseModel> models;

    public BackupValidationScheduler() {

        this.models = App.DataStore.getModelList();
    }


    public void fireValidationEvent(BaseModel model) {
        model = App.DataStore.getModelById(model.getUid());
        if(model.hasValidationJob()){
                    IFileValidationService hashService = new FileValidationService(model);
                    hashService.calculateAndSaveHashes();
            }
    }

    public void fireAllValidationEvents() {
        for(BaseModel modelInList : models) {
            if(modelInList.hasValidationJob()){
                if(modelInList.getValidationType() == ValidationTYPE.CRC32){
                    IFileValidationService hashService = new FileValidationService(modelInList);
                    hashService.calculateAndSaveHashes();
                }
            }
        }
    }



}
