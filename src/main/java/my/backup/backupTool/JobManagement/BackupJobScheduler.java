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
    private final HashMap<BaseModel,Thread> threadMap;
    private Thread timelineThread;

    private BackupJobScheduler() {
        this.threadMap = new HashMap<>();
        startTimelineThread();
    }

    private ReentrantLock threadLock = new ReentrantLock();

    public static BackupJobScheduler Singleton() {
        if(Instance == null) {
            synchronized (BackupJobScheduler.class) {
                Instance = new BackupJobScheduler();
            }
        }
        return Instance;
    }

    // Methode, um auf alle Backup-Threads zu warten
    public void waitForAllThreadsToFinish() {
        synchronized (threadMap) {
            for (Thread thread : threadMap.values()) {
                try {
                    thread.join();  // Wartet, bis der Thread beendet ist
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Warten auf Thread unterbrochen");
                }
            }
            threadMap.clear(); // Alle Threads aus der Map entfernen, nachdem sie abgeschlossen sind
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
            threadLock.lock();
            this.threadMap.put(model, mergeService.getThread());
            threadLock.unlock();
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


    /**
     * This Method calculates the next Backup Time using the Parameters in the Model.
     * Origin is LocalDateTime.now()
     *
     * @param model
     * @return LocalDateTime nextBackupTime
     */
    public LocalDateTime calculateNextBackupTime(BaseModel model) {

        if(!model.getCheckBoxStartDate() & !model.getCheckBoxDaysInterval() & !model.getCheckBoxHoursInterval()){
            return null;
        }

        if(     model.getCheckBoxStartDate() &
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


    public void startTimelineThread() {
        this.timelineThread = new Thread(JobTimeline.Singleton());
        JobTimeline.Singleton().setRunning(true);
        timelineThread.start();
        System.out.println("Timeline thread started");
    }

    public void stopTimelineThread() {
        JobTimeline.Singleton().setRunning(false); // Setzt die Bedingung in der Schleife auf false
        if (timelineThread != null) {
            timelineThread.interrupt(); // Falls er schläft, aufwecken
        }
    }


    public void updateTimeline() {
        this.stopTimelineThread();
        startTimelineThread();      // Starte einen neuen Thread
    }


}


