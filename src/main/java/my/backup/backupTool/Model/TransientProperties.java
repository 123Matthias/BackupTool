package my.backup.backupTool.Model;

import javafx.beans.property.*;
import my.backup.backupTool.Notifications.IMessageList;
import my.backup.backupTool.Notifications.MessageList;

import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransientProperties {

    private final DoubleProperty progressStateProp = new SimpleDoubleProperty(0.0);

    private DoubleProperty workingSpeedProperty = new SimpleDoubleProperty(0);

    private BooleanProperty isBackupSuccessfullyProperty = new SimpleBooleanProperty(false);

    private StringProperty validFilesCountProperty = new SimpleStringProperty("no value");

    private StringProperty totalFilesCountProperty = new SimpleStringProperty("no value");

    private StringProperty lastBackupTimeProperty = new SimpleStringProperty("no Date");

    private StringProperty nextBackupTimeProperty = new SimpleStringProperty("no Date");

    private IMessageList messageList;

    private SecretKey secretKey;

    private GCMParameterSpec initVector;


    public TransientProperties() {

        messageList = new MessageList();
    }

    public void createNewMessageList(){
        messageList = new MessageList();
    }

    public BooleanProperty getIsBackupSuccessfullyProperty() {
        return isBackupSuccessfullyProperty;
    }


    public IMessageList getMessageList() {
        return messageList;
    }

    public void setMessageList(IMessageList messages) {
        this.messageList =  messages;
    }

    public boolean isBackupValid(String source, String target) {
        return source.equals(target);
    }

    public double getWorkingSpeed() {
        return workingSpeedProperty.get();
    }
    public void setWorkingSpeed(double workingSpeed) {
        this.workingSpeedProperty.set(workingSpeed);
    }

    public DoubleProperty getWorkingSpeedProperty() {
        return this.workingSpeedProperty;
    }

    public double getProgressState() {
        return progressStateProp.get();
    }

    public void setProgressState(double progress) {
        this.progressStateProp.set(progress);
    }

    public DoubleProperty getProgressStateProperty() {
        return this.progressStateProp;
    }

    public StringProperty getTotalFilesCountProperty(){
        return this.totalFilesCountProperty;
    }

    public void setTotalFilesCountProperty(int value){
        this.totalFilesCountProperty.set(String.valueOf(value));
    }

    public StringProperty getValidFilesCountProperty(){
        return this.validFilesCountProperty;
    }

    public void setValidFilesCount(int value){
        this.validFilesCountProperty.set(String.valueOf(value));
    }

    public StringProperty getLastBackupTimeProperty() {
        return lastBackupTimeProperty;
    }

    public void setLastBackupTimeProperty(LocalDateTime lastBackupTime) {
        String dateString = "no Date";
        if (lastBackupTime != null) {
            DateTimeFormatter DateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            dateString = lastBackupTime.format(DateFormatter);
        }
        this.lastBackupTimeProperty.set(dateString);
    }

    public StringProperty getNextBackupTimeProperty() {
        return nextBackupTimeProperty;
    }

    public void setNextBackupTimeProperty(LocalDateTime nextBackupTime) {
        String dateString = "no Date";
        if (nextBackupTime != null) {
            DateTimeFormatter DateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            dateString = nextBackupTime.format(DateFormatter);
        }
        this.nextBackupTimeProperty.set(dateString);
    }

    public GCMParameterSpec getInitVector() {
        return this.initVector;
    }

    public void setInitVector(GCMParameterSpec initVector) {
        this.initVector = initVector;
    }

    public SecretKey getSecretKey(){
        return this.secretKey;
    }

    public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }
}
