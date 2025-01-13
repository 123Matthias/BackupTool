package my.backup.backupTool.Service;

import java.time.LocalDateTime;



public class TimeService implements ITimeService {
    public LocalDateTime setTiming(LocalDateTime startDate, int intervalDays, int intervalHours) {

        LocalDateTime calculateNextBackupTime = LocalDateTime.now();

        if (startDate != null && startDate.isAfter(LocalDateTime.now())) {
            calculateNextBackupTime = startDate;
        }
        if (intervalDays > 0) {
            calculateNextBackupTime = calculateNextBackupTime.plusDays(intervalDays);
        }
        if (intervalHours > 0) {
            calculateNextBackupTime = calculateNextBackupTime.plusHours(intervalHours);
        }

        return calculateNextBackupTime;
    }
}
