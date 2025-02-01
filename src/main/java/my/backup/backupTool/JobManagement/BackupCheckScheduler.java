package my.backup.backupTool.JobManagement;

import my.backup.backupTool.App;
import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.Model.HashTYPE;
import my.backup.backupTool.Service.FileValidationService;
import my.backup.backupTool.Service.IFileValidationService;

import java.util.ArrayList;
import java.util.List;

public class BackupCheckScheduler {

    List<BaseModel> models;
    List<BaseModel> validationOrderList;

    public BackupCheckScheduler() {

        this.models = App.DataStore.getModelList();
    }


    public void fireValidationEvent(BaseModel model) {
        model = App.DataStore.getModelById(model.getUid());
        if(model.hasHashOrder()){
                    IFileValidationService hashService = new FileValidationService(model);
                    hashService.calculateAndSaveHashes();
            }
    }


    public void fireAllValidationEvents() {
        for(BaseModel modelInList : models) {
            if(modelInList.hasHashOrder()){
                if(modelInList.getHashType() == HashTYPE.CRC32){
                    IFileValidationService hashService = new FileValidationService(modelInList);
                    hashService.calculateAndSaveHashes();
                }
            }
        }
    }



}
