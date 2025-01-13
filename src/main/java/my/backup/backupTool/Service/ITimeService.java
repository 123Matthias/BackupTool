package my.backup.backupTool.Service;

import java.time.LocalDateTime;

public interface ITimeService {
    LocalDateTime setTiming(LocalDateTime startDate, int intervalDays, int intervalHours);
}
