package my.backup.backupTool.JobManagement;

import my.backup.backupTool.App;
import my.backup.backupTool.Model.BaseModel;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class JobTimeline implements Runnable {

    private static JobTimeline Instance = null;
    private final AtomicBoolean running = new AtomicBoolean();
    private long sleepTimeInSeconds;
    private final Object lockObjTimeline = new Object();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private JobTimeline() {
        this.running.set(true);
    }

    public static JobTimeline Singleton() {
        if (Instance == null) {
            synchronized (JobTimeline.class) {
                Instance = new JobTimeline();
            }
        }
        return Instance;
    }

    @Override
    public void run() {
        while (running.get()) {
            this.fireAllScheduledBackups();
            this.sleepTimeInSeconds = this.calculateSleepTimeInSeconds();
//Wenn die Liste null ist brauche ich das lock nicht nur wenn sie einen inhalt hat.
            try {
                synchronized (lockObjTimeline) {
                    System.out.println("BEGIN SYNCRONIZED WAIT: " + this.sleepTimeInSeconds + " seconds");
                    System.out.println("BEGIN SYNCRONIZED WAIT: " + App.JobScheduler.getThreadMap().size());
                    if (App.JobScheduler.getThreadMap().size() == 0) {
                        this.startTimerWithEvent(sleepTimeInSeconds);
                    }

                    lockObjTimeline.wait();
                    System.out.println("END SYNCRONIZED WAIT: " + this.sleepTimeInSeconds + " seconds");
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            System.out.println("-------------------------------Thread: " + Thread.currentThread().getName() + ": " + this.sleepTimeInSeconds);
            try {
                System.out.println("JobTimeline Thread sleeping for: " + this.sleepTimeInSeconds + " seconds");
                Thread.sleep(this.sleepTimeInSeconds * 1000); //Millisekunden
                System.out.println("JobTimeline Thread sleeping completed." + "isRunning: " + this.running.get());
            } catch (InterruptedException e) {
                // Interrupt-Status wieder setzen
                Thread.currentThread().interrupt();  // Interrupt-Flag wieder setzen
                System.out.println("Thread was interrupted during sleep");
                return;  // Falls der Thread unterbrochen wurde, beende die Methode.
            }


        }
    }

    public void fireAllScheduledBackups() {
        for (BaseModel m : App.DataStore.getModelList()) {
            if (m.getNextBackupLocalDateTime() == null)
                continue;
            System.out.println("NEXT BACKUP TIME: " + m.getNextBackupLocalDateTime() + "\nLOCALDATETIME NOW: " + LocalDateTime.now());
            ;
            if (m.getNextBackupLocalDateTime().isBefore(LocalDateTime.now()) && m.hasBackupJob()) {
                App.JobScheduler.fireBackupEvent(m);
                System.out.println("JobTimeline Fire Backup Event");
            }


        }
    }


    private long calculateSleepTimeInSeconds() {
        long sleepTimeInSeconds = 86400;
        for (BaseModel m : App.DataStore.getModelList()) {
            if (m.getNextBackupLocalDateTime() == null)
                continue;
            LocalDateTime now = LocalDateTime.now();
            System.out.println("CALCULATED SLEEP TIME METHOD NOW VALUE: " + now);
            LocalDateTime next = m.getNextBackupLocalDateTime();
            System.out.println("NEXT BACKUP TIME: " + next);
            Duration duration = Duration.between(now, next);
            long seconds = duration.getSeconds();
            if (seconds < 0) {
                continue;
            }
            else if (seconds > 86400) {  // 86400 Sekunden = 24 Stunden
                sleepTimeInSeconds = 86400;
            } else if (seconds < sleepTimeInSeconds) {
                sleepTimeInSeconds = seconds;
            }
        }
        System.out.println("NEXT CALCULATED SLEEP TIME:" + sleepTimeInSeconds);
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


    // Stoppen des Threads
    public void notifyLock() {
        synchronized (lockObjTimeline) {
            System.out.println("JobTimeline Thread was locked");
            lockObjTimeline.notify();  // Falls der Thread im wait() ist, wecken
        }
    }


    private void startTimerWithEvent(long delayInSeconds) {
        // Task, der das Event auslöst, wenn der Timer abgelaufen ist
        Runnable eventTask = new Runnable() {
            @Override
            public void run() {
                System.out.println("Event ausgelöst nach " + delayInSeconds + " Sekunden!");
                notifyLock();
            }
        };

        // Timer planen, der nach 'delayInSeconds' Sekunden die 'eventTask' ausführt
        scheduler.schedule(eventTask, delayInSeconds, TimeUnit.SECONDS);
    }
}
