package my.backup.backupTool.JobManagement;

import my.backup.backupTool.App;
import my.backup.backupTool.Model.BackupType;
import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.Service.IMergeService;
import my.backup.backupTool.Service.MergeService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class BackupJobScheduler {

    private static volatile BackupJobScheduler Instance = null;
    private final HashMap<Thread,BaseModel> threadMap;
    private Thread timelineThread;
    public static JobTimeline Timeline;
    private final Object lockObjJobScheduler = new Object();

    private BackupJobScheduler() {
        Timeline = JobTimeline.Singleton();
        this.threadMap = new HashMap<>();
        startTimelineThread();
    }


    public static BackupJobScheduler Singleton() {
        if(Instance == null) {
            synchronized (BackupJobScheduler.class) {
                Instance = new BackupJobScheduler();
            }
        }
        return Instance;
    }

    // Methode, um auf alle Backup-Threads zu warten
    public synchronized void threadFinished(Thread thread) {
        this.threadMap.remove(thread);
        if(threadMap.isEmpty()) {
            Timeline.notifyLock();
            System.out.print("threadFinished DONE.");
        }


        System.out.println("Thread " + thread.getName() + " finished and removed from Threadmap");
        System.out.println("ThreadMAP SIZE:" + threadMap.size());

    }

    /**
     * Starts a new copying thread. It distinguishes between two copy methods: Merge or Full.
     * The operation also depends on the boolean condition hasBackupJob() in the model.
     * If hasBackupJob() is false, no backup event will be executed.
     *
     * @param model Contains all meta values required for the copy operation.
     */
    public synchronized boolean fireBackupEvent(BaseModel model) {
        System.out.println("Model JobScheduler: " + model);
        if(threadMap.containsValue(model)) {
            System.out.println("Model " + model + " ist bereits in Map ????!");
            return false;
        }
        if(model.getBackupType() == BackupType.MERGE && model.hasBackupJob()){
            IMergeService mergeService = new MergeService(model);
            mergeService.startMergeThread();
            this.threadMap.put(mergeService.getThread(), model);
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
        Iterator<Map.Entry<Thread, BaseModel>> iterator = threadMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Thread, BaseModel> entry = iterator.next();
            System.out.println("MapValues: " + entry.getKey() + ": " + entry.getValue());

            if (entry.getValue().getUid().equals(model.getUid())) {
                entry.getKey().interrupt();
                iterator.remove();  // Sicheres Entfernen während der Iteration
                System.out.println("Model JobScheduler: " + entry.getValue());
            }
        }
    }


    /**
     * This Method calculates the next Backup Time using the Parameters in the Model.
     * Origin is LocalDateTime.now()
     *
     * @param model
     * @return LocalDateTime nextBackupTime
     */
    public LocalDateTime calculateNextBackupTime(BaseModel model) {

        if(     !model.getCheckBoxStartDate() &
                !model.getCheckBoxDaysInterval() &
                !model.getCheckBoxHoursInterval() &
                !model.getCheckBoxMinutesInterval()){

            return null;
        }

        if(     model.getCheckBoxStartDate() &
                model.getStartDate() != null &
                model.getStartDate().isAfter(LocalDateTime.now()) &
                !model.getCheckBoxDaysInterval() &
                !model.getCheckBoxHoursInterval() &
                !model.getCheckBoxMinutesInterval()){

            return model.getStartDate();
        }

        LocalDateTime nextBackupTime = model.getStartDate() == null ? LocalDateTime.now() : model.getStartDate();

        if (model.getIntervalDays() <= 0) {
            model.setIntervalDays(0);
        }
        if (model.getIntervalHours() <= 0) {
            model.setIntervalHours(0);
        }

        if (model.getIntervalMinutes() <= 0) {
            model.setIntervalMinutes(0);
        }

        do {
            nextBackupTime = nextBackupTime.plusDays(model.getIntervalDays());
            nextBackupTime = nextBackupTime.plusHours(model.getIntervalHours());
            nextBackupTime = nextBackupTime.plusMinutes(model.getIntervalMinutes());

        } while (nextBackupTime.isBefore(LocalDateTime.now()));

        return nextBackupTime;
    }


    private void startTimelineThread() {
        this.timelineThread = new Thread(JobTimeline.Singleton());
        JobTimeline.Singleton().setRunning(true);
        timelineThread.start();
        System.out.println("Timeline thread started");
    }


    public HashMap<Thread, BaseModel> getThreadMap() {
        return threadMap;
    }
}


