package my.backup.backupTool.JobManagement;

import my.backup.backupTool.App;
import my.backup.backupTool.DataRepository.BaseDataStoreRepository;
import my.backup.backupTool.DataRepository.IDataStore;
import my.backup.backupTool.Model.BackupType;
import my.backup.backupTool.Model.IModel;
import my.backup.backupTool.Service.IMergeService;
import my.backup.backupTool.Service.MergeService;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BackupJobScheduler {

    List<IModel> modelList;
    List<IModel> backupOrderList;

    public BackupJobScheduler() {
        this.modelList = App.dataStore.getAllAsList();
        this.backupOrderList = createBackupList();
        this.fireBackupEvent();
    }


    public void fireBackupEvent() {
        for(IModel modelInList : backupOrderList) {
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

    public List<IModel> createBackupList(){
        List<IModel> backupList = new ArrayList<>();
        for(IModel model : this.modelList) {
            if (model.hasPlayBackupOrder()) {
                backupList.add(model);
            }
        }
        return backupList;
    }

    public void removeBackupOrderFromList(IModel model) {
        for(int i = 0; i < backupOrderList.size(); i++) {
            if(backupOrderList.get(i).getUid().equals(model.getUid())) {
                this.backupOrderList.remove(i);
                break;
            }
        }
    }

    public void addBackupOrderToList(IModel model) {
        this.backupOrderList.add(model);
    }

    public List<IModel> getBackupOrderList() {
        return backupOrderList;
    }

    public List<IModel> getModelList() {
        return modelList;
    }
}


