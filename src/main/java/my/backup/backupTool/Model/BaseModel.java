package my.backup.backupTool.Model;

import com.fasterxml.jackson.annotation.*;
import my.backup.backupTool.Service.IMessageList;
import my.backup.backupTool.Service.MessageList;

import java.io.File;
import java.time.LocalDateTime;

public class BaseModel implements IModel {



    @JsonProperty("uid")
    private String uid;

    @JsonProperty("title")
    private String title;
    @JsonProperty("source_path") // JSON Name wird geändert
    private String source;

    @JsonProperty("target_path") // JSON Name wird geändert
    private String target;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // Format für das Datum
    private LocalDateTime startDate;

    @JsonProperty("interval_days") // JSON Name wird geändert
    private int intervalDays;

    @JsonProperty("interval_hours") // JSON Name wird geändert
    private int intervalHours;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // Format für das Datum
    private LocalDateTime nextBackupLocalDateTime;

    @JsonIgnore
    private IMessageList messages;

    @JsonCreator
    public BaseModel() {
        messages = new MessageList();
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    // Getter und Setter
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
        return intervalDays;
    }

    public void setIntervalDays(int intervalDays) {
        this.intervalDays = intervalDays;
    }

    public int getIntervalHours() {
        return intervalHours;
    }

    public void setIntervalHours(int intervalHours) {
        this.intervalHours = intervalHours;
    }

    public LocalDateTime getNextBackupLocalDateTime() {
        return nextBackupLocalDateTime;
    }

    public void setNextBackupLocalDateTime(LocalDateTime nextBackupLocalDateTime) {
        this.nextBackupLocalDateTime = nextBackupLocalDateTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }




    public boolean validate(){
        messages = new MessageList();
        return validatePath() & validateDateTime() & validateIntervalDays() & validateIntervalHours();
    }


    private boolean validatePath() {

        boolean valid = true;

        File sourceDir = new File("");
        File targetDir = new File("");
        String sourceDisk = "";
        String targetDisk = "";

        if (source == null || target == null || source.length() <  3 || target.length() < 3){
            messages.addMessage("Quell oder Zielpfadeingabe ist zu kurz");
            valid = false;
        }
        else {
            sourceDisk = source.substring(0, 2);
            targetDisk = target.substring(0, 2);
            sourceDir = new File(sourceDisk);
            targetDir = new File(targetDisk);
        }


        if (!sourceDir.exists() || !targetDir.exists()) {
            System.out.println("Source: " + sourceDir);
            System.out.println("Target" + targetDir);
            messages.addMessage("SOURCE OR TARGET DIRECTORY not EXISTS.");
            valid = false;
        }

        if (!sourceDisk.isEmpty() && sourceDisk.equalsIgnoreCase(targetDisk)) {
            messages.addMessage("SOURCE " + sourceDisk + " + AND TARGET " + targetDisk + " IS ON SAME DISK");
            valid = false;
        }
        return valid;
    }

    private boolean validateDateTime(){
        if (startDate == null || startDate.isBefore(LocalDateTime.now())){
            messages.addMessage("BACKUP DATE has to be in FUTURE.");
            return false;
        }
        return true;
    }

    private boolean validateIntervalHours(){
        if(intervalHours < 0){
            messages.addMessage("interval HOURS has to be  non NEGATIVE.");
            return false;
        }
        return true;
    }

    private boolean validateIntervalDays(){
        if(intervalHours < 0) {
            messages.addMessage("interval DAYS has to be non NEGATIVE.");
            return false;
        }
        return true;
    }

    @Override
    @JsonIgnore
    public IMessageList getMessageList() {
        return messages;
    }
}
