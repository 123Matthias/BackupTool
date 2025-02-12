package my.backup.backupTool.JobManagement;

import my.backup.backupTool.App;
import my.backup.backupTool.Model.BaseModel;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class JobTimeline implements Runnable {

    private static JobTimeline Instance = null;
    private final AtomicBoolean running = new AtomicBoolean();
    private long sleepTimeInSeconds;
    private static final long DURATION_MAX_SECONDS = 86400;
    private final Object lockObjTimeline = new Object();
    private List<BaseModel> storedList = null;
    private final IFireBackupEvent JobSchedulerCallback;

    private JobTimeline() {
        JobSchedulerCallback = App.JobScheduler;
        this.running.set(true);
        this.storedList = App.DataStore.getModelList();
    }

    public static JobTimeline Singleton() {
        if (Instance == null) {
            synchronized (JobTimeline.class) {
                if(Instance == null) {
                    Instance = new JobTimeline();
                }
            }
        }
        return Instance;
    }

    @Override
    public void run() {
        while (running.get()) {
           this.fireAllScheduledBackups();
           try {
                synchronized (lockObjTimeline) {
                    while(!App.JobScheduler.getThreadMap().isEmpty()) {
                        //Every 60 Seconds check Time. This may be not necessary. Finished Threads remove themselves from Map.
                        App.JobScheduler.checkThreadStates();
                        lockObjTimeline.wait(60000);
                        System.out.println("LOCK OBJECT LIST IS EMPTY RELEASES");
                    }
                }
               this.sleepTimeInSeconds = this.calculateSleepInSeconds();
                //Thread sleeps until the next Backup Time is reached.
               System.out.println("THREAD IS GOING TO SLEEP: " + this.sleepTimeInSeconds + "\n\n\n");
                Thread.sleep(this.sleepTimeInSeconds * 1000); //Millisekunden
               System.out.println("THREAD WAKES UP");
           }

           catch (InterruptedException e) {
               System.out.println("  -- >> THREAD: " + Thread.currentThread().isInterrupted() + " ThreadState: " + Thread.currentThread().getState());
               System.out.println("THREAD INTERRUPTED IAM WAKING UP");


           }


        }
    }

    public void fireAllScheduledBackups() {
        this.storedList = App.DataStore.getModelList();
        for (BaseModel m : this.storedList) {
            if (m.getNextBackupLocalDateTime() == null)
                continue;
            if (m.hasBackupJob() && m.getNextBackupLocalDateTime().isBefore(LocalDateTime.now())) {
                JobSchedulerCallback.fireBackupEvent(m);
            }
        }
    }


    private long calculateSleepInSeconds() {
        long sleepTimeInSeconds = DURATION_MAX_SECONDS;
        for (BaseModel m : this.storedList) {
            if (m.hasBackupJob() == false || m.getNextBackupLocalDateTime() == null) {
                continue;
            }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next = m.getNextBackupLocalDateTime();
        Duration duration = Duration.between(now, next);
        long durationInSeconds = duration.getSeconds();

        // Waiting for result is possible but may block the Scheduler Timeline if something happens with a Backup INSTANCE.
        // if nextBackupTime is not calculated the Schedules goes on with 1 second steps. But this is not posssible only if there is at the end of the Service job.
        // Maybe a check if last backup time changed but 1 second steps until values are updated works.
        // The new Backup Time is Calculated at the end of a Backup Job where  i notify the wait lock if all Backup Threads are finished in the HashMap.
        // If Scheduler is faster than save JSON and update repository list the getter DataStore.getModelList() is not up to date.
        if (durationInSeconds <= 0) {
            sleepTimeInSeconds = 1;
        }
        if (durationInSeconds > 0 && durationInSeconds < sleepTimeInSeconds) {
            sleepTimeInSeconds = durationInSeconds;
        }
        else if (durationInSeconds > DURATION_MAX_SECONDS) {  // 86400 Sekunden = 24 Stunden
            sleepTimeInSeconds = DURATION_MAX_SECONDS;}
        }
        System.out.println("NEXT CALCULATED SLEEP TIME: " + sleepTimeInSeconds + " seconds");
        return sleepTimeInSeconds + 2; //2Sekunden Puffer wegen Sekunden runden
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


    // Stoppen des Threads
    public void notifyLock() {
        synchronized (lockObjTimeline) {
            lockObjTimeline.notifyAll();
        }
    }
}
