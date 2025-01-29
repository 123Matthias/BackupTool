package my.backup.backupTool.JobManagement;

import my.backup.backupTool.App;
import my.backup.backupTool.Model.IModel;
import my.backup.backupTool.Model.HashTYPE;
import my.backup.backupTool.Service.FileValidationService;
import my.backup.backupTool.Service.IFileValidationService;

import java.util.ArrayList;
import java.util.List;

public class BackupCheckScheduler {

    List<IModel> models;
    List<IModel> validationOrderList;

    public BackupCheckScheduler() {

        this.models = App.dataStore.getAllAsList();
        this.validationOrderList = createValidationList();
    }


    public void fireValidationEvent(IModel model) {
            if(model.hasHashOrder()){
                    IFileValidationService hashService = new FileValidationService(model);
                    hashService.calculateAndSaveHashes();
            }
    }


    public void fireAllValidationEvents() {
        for(IModel modelInList : models) {
            if(modelInList.hasHashOrder()){
                if(modelInList.getHashType() == HashTYPE.CRC32){
                    IFileValidationService hashService = new FileValidationService(modelInList);
                    hashService.calculateAndSaveHashes();
                }
            }
        }
    }


    public List<IModel> createValidationList(){
        validationOrderList = new ArrayList<>();
        for(IModel model : this.models) {
            if (model.hasHashOrder()) {
                validationOrderList.add(model);
            }
        }
        return validationOrderList;
    }

    public void removeValidationOrderFromList(IModel model) {
        for(int i = 0; i < validationOrderList.size(); i++) {
            if(validationOrderList.get(i).getUid().equals(model.getUid())) {
                this.validationOrderList.remove(i);
                break;
            }
        }
    }

    public void addValidationOrderToList(IModel model) {
        this.validationOrderList.add(model);
    }



}
