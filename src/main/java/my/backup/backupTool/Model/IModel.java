package my.backup.backupTool.Model;

import java.time.LocalDateTime;

public interface IModel {

    String getUid();

    void setUid(String uid);
    String getSource();
    void setSource(String source);

    String getTarget();
    void setTarget(String target);

    LocalDateTime getStartDate();
    void setStartDate(LocalDateTime startDate);

    int getIntervalDays();
    void setIntervalDays(int intervalDays);

    int getIntervalHours();
    void setIntervalHours(int intervalHours);

    LocalDateTime getNextBackupLocalDateTime();
    void setNextBackupLocalDateTime(LocalDateTime nextBackupLocalDateTime);

    String getTitle();

    void setTitle(String title);
}
