package my.backup.backupTool.JobManagement;

import my.backup.backupTool.App;
import my.backup.backupTool.DataRepository.BaseDataStoreRepository;
import my.backup.backupTool.Model.BackupType;
import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.Service.IMergeService;
import my.backup.backupTool.Service.MergeService;
import my.backup.backupTool.Service.TimeService;

public class BackupJobScheduler {

    private static volatile BackupJobScheduler Instance = null;

    private BackupJobScheduler() {

    }

    public static BackupJobScheduler Singleton() {
        if(Instance == null) {
            synchronized (BackupJobScheduler.class) {
                Instance = new BackupJobScheduler();
            }
        }
        return Instance;
    }

    /**
     * Starts a new copying thread for all Backup Jobs in the List of Models. It distinguishes between two copy methods: Merge or Full.
     * The operation also depends on the boolean condition hasBackupJob() in the model.
     * If hasBackupJob() is false, no backup event will be executed.
     */
    public void fireAllBackupEvents() {
        for(BaseModel model : App.DataStore.getModelList()) {
                if(model.getBackupType() == BackupType.MERGE && model.hasBackupJob()){
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

    /**
     * Starts a new copying thread. It distinguishes between two copy methods: Merge or Full.
     * The operation also depends on the boolean condition hasBackupJob() in the model.
     * If hasBackupJob() is false, no backup event will be executed.
     *
     * @param model Contains all meta values required for the copy operation.
     */
    public void fireBackupEvent(BaseModel model) {
            model = App.DataStore.getModelById(model.getUid());
            System.out.println("Model JobScheduler: " + model);
            if(model.getBackupType() == BackupType.MERGE && model.hasBackupJob()){
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


