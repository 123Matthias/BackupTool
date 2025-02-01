package my.backup.backupTool.Service;

import java.time.LocalDateTime;



public class TimeService implements ITimeService {
    public LocalDateTime setTiming(LocalDateTime startDate, int intervalDays, int intervalHours) {

        LocalDateTime nextBackupTime = startDate == null ? LocalDateTime.now() : startDate;

        if (intervalDays > 0) {
            nextBackupTime = nextBackupTime.plusDays(intervalDays);
        }
        if (intervalHours > 0) {
            nextBackupTime = nextBackupTime.plusHours(intervalHours);
        }

        return nextBackupTime;
    }
}
