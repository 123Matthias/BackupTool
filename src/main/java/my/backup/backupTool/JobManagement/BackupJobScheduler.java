package my.backup.backupTool.JobManagement;

import my.backup.backupTool.App;
import my.backup.backupTool.DataRepository.BaseDataStoreRepository;
import my.backup.backupTool.DataRepository.IDataStore;
import my.backup.backupTool.Model.BackupType;
import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.Service.IMergeService;
import my.backup.backupTool.Service.MergeService;
import my.backup.backupTool.Service.TimeService;


import java.util.ArrayList;
import java.util.List;

public class BackupJobScheduler {

    TimeService timeService = new TimeService();

    public BackupJobScheduler() {
      //  this.fireBackupEvent();
    }




    public void fireAllBackupEvents() {
        for(BaseModel model : App.DataStore.getModelList()) {
                if(model.getBackupType() == BackupType.MERGE && model.hasplaybackuporder()){
                    IMergeService mergeService = new MergeService(model);
                    mergeService.startMergeThread();
                    System.out.println("Model JobScheduler: " + model);

                    System.out.println("----------Merge Service in Job Scheduler started--------------");
                }
                if(model.getBackupType() == BackupType.FULL){
                    //TODO
                }
                if(model.getBackupType() == BackupType.SYNCHRONIZED){
                    //TODO
                }

        }
    }

    public void fireBackupEvent(BaseModel model) {
            model = App.DataStore.getModelById(model.getUid());
            if(model.getBackupType() == BackupType.MERGE && model.hasplaybackuporder()){
                IMergeService mergeService = new MergeService(model);
                mergeService.startMergeThread();
                System.out.println("Model JobScheduler: " + model);

                System.out.println("----------Merge Service in Job Scheduler started--------------");
            }
            if(model.getBackupType() == BackupType.FULL){
                //TODO
            }
            if(model.getBackupType() == BackupType.SYNCHRONIZED){
                //TODO
            }
        }



}


