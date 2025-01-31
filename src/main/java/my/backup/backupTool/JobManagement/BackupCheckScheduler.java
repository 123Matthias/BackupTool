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

        this.models = App.DataStore.getAllAsList();
        this.validationOrderList = createValidationList();
    }


    public void fireValidationEvent(BaseModel model) {
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


    public List<BaseModel> createValidationList(){
        validationOrderList = new ArrayList<>();
        for(BaseModel model : this.models) {
            if (model.hasHashOrder()) {
                validationOrderList.add(model);
            }
        }
        return validationOrderList;
    }

    public void removeValidationOrderFromList(BaseModel model) {
        for(int i = 0; i < validationOrderList.size(); i++) {
            if(validationOrderList.get(i).getUid().equals(model.getUid())) {
                this.validationOrderList.remove(i);
                break;
            }
        }
    }

    public void addValidationOrderToList(BaseModel model) {
        this.validationOrderList.add(model);
    }



}
