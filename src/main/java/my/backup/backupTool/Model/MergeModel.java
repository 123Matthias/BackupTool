package my.backup.backupTool.Model;

import java.time.LocalDateTime;

public class MergeModel {

    private String source;
    private String target;
    private LocalDateTime startDate;
    private int IntervallDays;
    private int IntervallHours;

    private LocalDateTime nextBackup;

    public MergeModel(){

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

    public int getIntervallDays() {
        return IntervallDays;
    }

    public void setIntervallDays(int intervallDays) {
        IntervallDays = intervallDays;
    }

    public int getIntervallHours() {
        return IntervallHours;
    }

    public void setIntervallHours(int intervallHuers) {
        IntervallHours = intervallHuers;
    }

    public LocalDateTime getNextBackup() {
        return nextBackup;
    }

    public void setNextBackup(LocalDateTime nextBackup) {
        this.nextBackup = nextBackup;
    }
}
