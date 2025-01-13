package my.backup.backupTool.Model;

import java.time.LocalDateTime;

public class BaseModel implements IModel {

    private String source;
    private String target;
    private LocalDateTime startDate;
    private int IntervalDays;
    private int IntervalHours;
    private LocalDateTime nextBackupLocalDateTime;

    public BaseModel(){

    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public int getIntervalDays() {
        return IntervalDays;
    }

    public void setIntervalDays(int intervalDays) {
        IntervalDays = intervalDays;
    }

    public int getIntervalHours() {
        return IntervalHours;
    }

    public void setIntervalHours(int intervallHuers) {
        IntervalHours = intervallHuers;
    }

    public LocalDateTime getNextBackupLocalDateTime() {
        return nextBackupLocalDateTime;
    }

    public void setNextBackupLocalDateTime(LocalDateTime nextBackupLocalDateTime) {
        this.nextBackupLocalDateTime = nextBackupLocalDateTime;
    }
}
