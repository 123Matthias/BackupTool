package my.backup.backupTool.JobManagement;

import my.backup.backupTool.App;
import my.backup.backupTool.Model.BaseModel;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class JobTimeline implements Runnable {

    private static JobTimeline Instance = null;
    private final AtomicBoolean running = new AtomicBoolean();
    private long sleepTimeInSeconds;


    private JobTimeline() {
        this.running.set(true);
    }

    public static JobTimeline Singleton(){
        if(Instance == null) {
            synchronized (JobTimeline.class) {
                Instance = new JobTimeline();
            }
        }
        return Instance;
    }

    @Override
    public void run() {
        while(running.get()) {
            this.sleepTimeInSeconds = this.calculateSleepTimeInSeconds();
            try {
                System.out.println("JobTimeline Thread sleeping for: " + this.sleepTimeInSeconds + " seconds");
                Thread.sleep(this.sleepTimeInSeconds * 1000); //Millisekunden
                this.getModelsReadyForBackupJob();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("JobTimeline Thread interrupted");
                break;
            }
        }
    }

    public List<BaseModel> getModelsReadyForBackupJob(){
        for(BaseModel m : App.DataStore.getModelList()){
            if(m.getNextBackupLocalDateTime().isBefore(LocalDateTime.now()) && m.hasBackupJob()){
                App.JobScheduler.fireBackupEvent(m);
            }

        }
        App.JobScheduler.waitForAllThreadsToFinish();
        return App.DataStore.getModelList();
    }

    private long calculateSleepTimeInSeconds(){
        long sleepTimeInSeconds = 86400;
        for(BaseModel m : App.DataStore.getModelList()){
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime next = m.getNextBackupLocalDateTime();
            Duration duration = Duration.between(now, next);
            long seconds = duration.getSeconds();
            if (seconds < 0) {
                continue;
            }

            else if (seconds > 86400) {  // 86400 Sekunden = 24 Stunden
                sleepTimeInSeconds = 86400;
            }

            else if(seconds < sleepTimeInSeconds){
                sleepTimeInSeconds = seconds;
            }
        }
        return sleepTimeInSeconds;
    }

    public boolean isRunning() {
        return this.running.get();
    }

    public void setRunning(boolean running) {
        this.running.set(running);

    }

    public long getSleepTimeInSeconds() {
        return sleepTimeInSeconds;
    }

    public void setSleepTimeInSeconds(long sleepTimeInSeconds) {
        this.sleepTimeInSeconds = sleepTimeInSeconds;
    }


}
