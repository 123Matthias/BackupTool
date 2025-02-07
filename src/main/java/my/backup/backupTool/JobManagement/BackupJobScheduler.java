package my.backup.backupTool.JobManagement;

import my.backup.backupTool.App;
import my.backup.backupTool.Model.BackupType;
import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.Service.IMergeService;
import my.backup.backupTool.Service.MergeService;

import java.time.LocalDateTime;
import java.util.*;

public class BackupJobScheduler {

    private static volatile BackupJobScheduler Instance = null;
    private HashMap<BaseModel,Thread> threadMap;

    private BackupJobScheduler() {
        this.threadMap = new HashMap<>();
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
    public boolean fireBackupEvent(BaseModel model) {
        System.out.println("Model JobScheduler: " + model);
        if(model.getBackupType() == BackupType.MERGE && model.hasBackupJob()){
            IMergeService mergeService = new MergeService(model);
            mergeService.startMergeThread();
            this.threadMap.put(model, mergeService.getThread());
            System.out.println("Thread in list: " + mergeService.getThread().getName());
            return true;
        }
        if(model.getBackupType() == BackupType.FULL){
            //TODO
        }
        if(model.getBackupType() == BackupType.SYNCHRONIZED){
            //TODO
        }
        return false;
    }

    public void stopAndInterruptBackupEvent(BaseModel model) {
        model.setBackupJob(false);
        Iterator<Map.Entry<BaseModel, Thread>> iterator = threadMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BaseModel, Thread> entry = iterator.next();
            System.out.println("MapValues: " + entry.getKey() + ": " + entry.getValue());

            if (entry.getKey().getUid().equals(model.getUid())) {
                entry.getValue().interrupt();
                iterator.remove();  // Sicheres Entfernen während der Iteration
                System.out.println("Model JobScheduler: " + entry.getValue());
            }
        }
    }

    public LocalDateTime calculateNextBackupTime(LocalDateTime startDate, int intervalDays, int intervalHours) {

        LocalDateTime nextBackupTime = startDate == null ? LocalDateTime.now() : startDate;

        if (intervalDays <= 0) {
            return nextBackupTime;
        }
        if (intervalHours <= 0) {
            return nextBackupTime;
        }

        do {
            nextBackupTime = nextBackupTime.plusDays(intervalDays);
            nextBackupTime = nextBackupTime.plusHours(intervalHours);

        } while (nextBackupTime.isBefore(LocalDateTime.now()));

        return nextBackupTime;
    }

    public LocalDateTime calculateLastBackupTime(LocalDateTime nextBackupTime) {

            LocalDateTime lastBackupTime = nextBackupTime;
            return lastBackupTime;
        }

}


