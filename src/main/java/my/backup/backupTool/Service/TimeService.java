package my.backup.backupTool.Service;

import my.backup.backupTool.App;

import java.time.LocalDateTime;



public class TimeService {

    public static LocalDateTime calculateNextBackupTime(LocalDateTime startDate, int intervalDays, int intervalHours) {

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

    public static LocalDateTime calculateLastBackupTime(LocalDateTime nextBackupTime) {

        LocalDateTime lastBackupTime = nextBackupTime;
        return lastBackupTime;
    }
}
