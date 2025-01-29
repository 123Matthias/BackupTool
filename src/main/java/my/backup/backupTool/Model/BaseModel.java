package my.backup.backupTool.Model;

import com.fasterxml.jackson.annotation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import my.backup.backupTool.Service.IMessageList;
import my.backup.backupTool.Service.MessageList;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.time.LocalDateTime;

public class BaseModel implements IModel {


    @JsonIgnore
    private final DoubleProperty progressStateProp = new SimpleDoubleProperty(0.0);

    @JsonProperty("uid")
    private String uid;

    @JsonProperty("backup_Type")
    private BackupType backupType;

    @JsonProperty("flowPane-Position")
    private int flowPanePosition;

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

    @JsonProperty("Cardwidth")
    private int cardWidth;

    @JsonProperty("play-BackupOrder")
    private boolean playBackupOrder;


    @JsonIgnore
    private StringProperty sourceHashProperty = new SimpleStringProperty("no value");

    @JsonIgnore
    private StringProperty targetHashProperty = new SimpleStringProperty("no value");

    @JsonProperty("hash_Order")
    private boolean hashOrder;

    @JsonProperty("hash_Type")
    private HashTYPE hashType;

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

    public BackupType getBackupType() {
        return this.backupType;
    }

    public void setBackupType(BackupType backupType) {
        this.backupType = backupType;
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

        if (source == null || source.length() <  3){
            messages.addMessage("Quellpfadeingabe ist zu kurz");
            valid = false;
        }

        if (target == null || target.length() <  3){
            messages.addMessage("Zielpfadeingabe ist zu kurz");
            valid = false;
        }

        if(!valid){
            return valid;
        }

        File sourceDir = new File(source);
        File targetDir = new File(target);

        if (!sourceDir.exists()) {
            System.out.println("Source: " + sourceDir);
            messages.addMessage("SOURCE DIRECTORY not EXISTS.");
            valid = false;
        }

        if (!targetDir.exists()) {
            System.out.println("Target" + targetDir);
            messages.addMessage("TARGET DIRECTORY not EXISTS.");
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

    public int getFlowPanePosition() {
        return flowPanePosition;
    }

    public void setFlowPanePosition(int flowPanePosition) {
        this.flowPanePosition = flowPanePosition;
    }

    public int getCardWidth() {
        return cardWidth;
    }

    public void setCardWidth(int cardWidth) {
        this.cardWidth = cardWidth;
    }

    @Override
    @JsonIgnore
    public IMessageList getMessageList() {
        return messages;
    }


    @JsonProperty("play-BackupOrder")
    public boolean hasPlayBackupOrder() {
        return playBackupOrder;
    }
    @JsonProperty("play-BackupOrder")
    public void setPlayBackupOrder(boolean playBackupOrder) {
        this.playBackupOrder = playBackupOrder;
    }


    @Override
    @JsonIgnore
    public double getProgressState() {
        return progressStateProp.get();
    }

    @Override
    @JsonIgnore
    public void setProgressState(double progress) {
        this.progressStateProp.set(progress);

    }

    @Override
    @JsonIgnore
    public DoubleProperty getProgressStateProperty() {
        return this.progressStateProp;
    }

    // Getter für die Serialisierung
    @JsonProperty("source-hash-property")
    public String getSourceHash() {
        return sourceHashProperty.get();
    }

    @JsonProperty("target_Hash_property")
    public String getTargetHash() {
        return targetHashProperty.get();
    }

    // Setter für die Deserialisierung
    @JsonProperty("source-hash-property")
    public void setSourceHash(String value) {
        this.sourceHashProperty.set(value);
    }

    @JsonProperty("target_Hash_property")
    public void setTargetHash(String value) {
        this.targetHashProperty.set(value);
    }

    @JsonIgnore
    public StringProperty getTargetHashProperty(){
        return this.targetHashProperty;
    }

    @JsonIgnore
    public StringProperty getSourceHashProperty(){
        return this.sourceHashProperty;
    }



    @JsonProperty("hash_Order")
    public boolean hasHashOrder() {
        return hashOrder;
    }
    @JsonProperty("hash_Order")
    public void setHashOrder(boolean hashOrder) {
        this.hashOrder = hashOrder;
    }


    @JsonProperty("hash_Type")
    public HashTYPE getHashType() {
        return hashType;
    }


    @JsonProperty("hash_Type")
    public void setHashType(HashTYPE hashType) {
        this.hashType = hashType;
    }


}
