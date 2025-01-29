package my.backup.backupTool.JobManagement;

import my.backup.backupTool.DataRepository.BaseDataRepository;
import my.backup.backupTool.DataRepository.IStoreData;
import my.backup.backupTool.Model.BackupType;
import my.backup.backupTool.Model.IModel;
import my.backup.backupTool.Service.IMergeService;
import my.backup.backupTool.Service.MergeService;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BackupJobScheduler implements Subjekt {

    List<IModel> models;
    List<IModel> backupOrderList;
    IStoreData dataStore;

    public BackupJobScheduler() {
        dataStore = new BaseDataRepository();

        try {
            this.models = dataStore.getAllAsList();
        } catch (IOException e) {
            this.models = new ArrayList<>();
        }
        this.backupOrderList = createBackupList();
        this.fireBackupEvent();
    }
    @Override
    public void add(IModel model) {
        models.add(model);
    }

    @Override
    public void remove(IModel model) {
        for(IModel modelInList : models) {
            if(modelInList.getUid().equals(model.getUid()))
                models.remove(modelInList);
        }
    }

    @Override
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
        for(IModel model : this.models) {
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

    public IStoreData getDataStore() {
        return dataStore;
    }

    public List<IModel> getBackupOrderList() {
        return backupOrderList;
    }

    public List<IModel> getModels() {
        return models;
    }
}


