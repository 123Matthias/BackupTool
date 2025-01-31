package my.backup.backupTool.JobManagement;

import my.backup.backupTool.App;
import my.backup.backupTool.Model.BackupType;
import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.Service.IMergeService;
import my.backup.backupTool.Service.MergeService;


import java.util.ArrayList;
import java.util.List;

public class BackupJobScheduler {

    List<BaseModel> modelList;
    List<BaseModel> backupOrderList;

    public BackupJobScheduler() {
        this.modelList = App.DataStore.getAllAsList();
        this.backupOrderList = createBackupList();
        this.fireBackupEvent();
    }


    public void fireBackupEvent() {
        for(BaseModel modelInList : backupOrderList) {
                if(modelInList.getBackupType() == BackupType.MERGE){
                    IMergeService mergeService = new MergeService(modelInList);
                    mergeService.startMergeThread();
                    System.out.println("----------Merge Service in Job Scheduler started--------------");
                }
                if(modelInList.getBackupType() == BackupType.FULL){
                    //TODO
                }
                if(modelInList.getBackupType() == BackupType.SYNCHRONIZED){
                    //TODO
                }

        }
    }

    public List<BaseModel> createBackupList(){
        List<BaseModel> backupList = new ArrayList<>();
        for(BaseModel model : this.modelList) {
            if (model.hasPlayBackupOrder()) {
                backupList.add(model);
            }
        }
        return backupList;
    }

    public void removeBackupOrderFromList(BaseModel model) {
        for(int i = 0; i < backupOrderList.size(); i++) {
            if(backupOrderList.get(i).getUid().equals(model.getUid())) {
                this.backupOrderList.remove(i);
                break;
            }
        }
    }

    public void addBackupOrderToList(BaseModel model) {
        this.backupOrderList.add(model);
    }

    public List<BaseModel> getBackupOrderList() {
        return backupOrderList;
    }

    public List<BaseModel> getModelList() {
        return modelList;
    }
}


