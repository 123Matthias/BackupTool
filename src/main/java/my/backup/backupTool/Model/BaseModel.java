package my.backup.backupTool.Model;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import javafx.beans.property.*;
import my.backup.backupTool.Service.IMessageList;
import my.backup.backupTool.Service.MessageList;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.time.LocalDateTime;

public class BaseModel {


    @JsonProperty("uid")
    private String uid;

    @JsonProperty("backup_type")
    private BackupType backupType;

    @JsonProperty("flowPane-Position")
    private int flowPanePosition;

    @JsonProperty("title")
    private String title;

    @JsonProperty("source-path") // JSON Name wird geändert
    private String source;

    @JsonProperty("target-path") // JSON Name wird geändert
    private String target;

    @JsonProperty("source-validation")
    private String sourceValidationValue;

    @JsonProperty("target-validation")
    private String targetValidationValue;

    @JsonProperty("checkBox-startDate")
    private boolean checkBoxStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // Format für das Datum
    private LocalDateTime startDate;

    @JsonProperty("checkBox-daysInterval")
    private boolean checkBoxDaysInterval;

    @JsonProperty("interval-days") // JSON Name wird geändert
    private int intervalDays;

    @JsonProperty("checkBox-hoursInterval")
    private boolean checkBoxHoursInterval;

    @JsonProperty("interval-hours") // JSON Name wird geändert
    private int intervalHours;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // Format für das Datum
    private LocalDateTime lastBackupLocalDateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // Format für das Datum
    private LocalDateTime nextBackupLocalDateTime;

    @JsonProperty("card-width")
    private int cardWidth;

    @JsonProperty("backup-job")
    private boolean backupJob;

    @JsonProperty("encryption-job")
    private boolean encryptionJob;

    @JsonProperty("restore-mode")
    private boolean restoreMode;

    @JsonProperty("checkBox-validationJob")
    private boolean checkBoxValidationJob;

    @JsonProperty("validation-job")
    private boolean validationJob;

    @JsonProperty("validation-type")
    private ValidationTYPE validationType;

    @JsonProperty("encryption-type")
    private EncryptionTYPE encryptionType;

    @JsonProperty("secret-key")
    @JsonSerialize(using = MyJackson.SecretKeySerializer.class)
    @JsonDeserialize(using = MyJackson.SecretKeyDeserializer.class)
    private SecretKey secretKey;

    @JsonProperty("init-vector")
    @JsonSerialize(using = MyJackson.IvParameterSpecSerializer.class)
    @JsonDeserialize(using = MyJackson.IvParameterSpecDeserializer.class)
    private IvParameterSpec initVector;

    @JsonProperty("checkBox-encryptionJob")
    private boolean checkBoxEncryptionJob;

    @JsonIgnore
    private TransientProperties transientProperties;


    @JsonCreator
    public BaseModel() {
        this.transientProperties = new TransientProperties();
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

    public LocalDateTime getLastBackupLocalDateTime() {
        return lastBackupLocalDateTime;
    }

    public void setLastBackupLocalDateTime(LocalDateTime lastBackupLocalDateTime) {
        this.lastBackupLocalDateTime = lastBackupLocalDateTime;
    }

    public boolean getCheckBoxHoursInterval() {
        return checkBoxHoursInterval;
    }

    public void setCheckBoxHoursInterval(boolean checkBoxHoursInterval) {
        this.checkBoxHoursInterval = checkBoxHoursInterval;
    }

    public boolean getCheckBoxDaysInterval() {
        return checkBoxDaysInterval;
    }

    public void setCheckBoxDaysInterval(boolean checkBoxDaysInterval) {
        this.checkBoxDaysInterval = checkBoxDaysInterval;
    }

    public boolean getCheckBoxStartDate() {
        return checkBoxStartDate;
    }

    public void setCheckBoxStartDate(boolean checkBoxStartDate) {
        this.checkBoxStartDate = checkBoxStartDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    @JsonProperty("backup-job")
    public boolean hasBackupJob() {
        return backupJob;
    }
    @JsonProperty("backup-job")
    public void setBackupJob(boolean backupJob) {
        this.backupJob = backupJob;
    }

    public boolean getCheckBoxValidationJob() {
        return checkBoxValidationJob;
    }

    public void setCheckBoxValidationJob(boolean checkBoxValidationJob) {
        this.checkBoxValidationJob = checkBoxValidationJob;
    }

    public boolean getCheckBoxEncryptionJob() {
        return checkBoxEncryptionJob;
    }

    public void setCheckBoxEncryptionJob(boolean checkBoxEncryptionJob) {
        this.checkBoxEncryptionJob = checkBoxEncryptionJob;
    }


    public String getSourceValidationValue() {
        return sourceValidationValue;
    }

    public void setSourceValidationValue(String value) {
        this.getTransientProperties().setSourceValidationProperty(value);
        this.sourceValidationValue = value;
    }

    public String getTargetValidationValue() {
        return targetValidationValue;
    }

    public void setTargetValidationValue(String value) {
        this.getTransientProperties().setTargetValidationProperty(value);
        this.targetValidationValue = value;
    }

    @JsonProperty("validation-job")
    public boolean hasValidationJob() {
        return validationJob;
    }

    @JsonProperty("validation-job")
    public void setValidationJob(boolean validationJob) {
        this.validationJob = validationJob;
    }

    @JsonProperty("validation-type")
    public ValidationTYPE getValidationType() {
        return validationType != null ? validationType : ValidationTYPE.NONE;
    }

    @JsonProperty("validation-type")
    public void setValidationType(ValidationTYPE validationType) {
        this.validationType = validationType;
    }

    @JsonProperty("encryption-type")
    public EncryptionTYPE getEncryptionType() {
        return encryptionType;
    }

    @JsonProperty("encryption-type")
    public void setEncryptionTYPE(EncryptionTYPE encryptionType) {
        this.encryptionType = encryptionType;
    }

    public IvParameterSpec getInitVector() {
        return initVector;
    }

    public void setInitVector(IvParameterSpec initVector) {
        this.initVector = initVector;
    }

    @JsonProperty("secret-key")
    public SecretKey getSecretKey() {
        return secretKey;
    }

    @JsonProperty("secret-key")
    public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    @JsonProperty("encryption-job")
    public boolean hasEncryptionJob() {
        return encryptionJob;
    }

    @JsonProperty("encryption-job")
    public void setEncryptionJob(boolean encryptionJob) {
        this.encryptionJob = encryptionJob;
    }

    @JsonProperty("restore-mode")
    public boolean isRestoreMode() {
        return restoreMode;
    }

    @JsonProperty("restore-mode")
    public void setRestoreMode(boolean restoreMode) {
        this.restoreMode = restoreMode;
    }

    public TransientProperties getTransientProperties() {
        return transientProperties;
    }

    public boolean validate(){
        this.transientProperties.setMessageList(new MessageList());
        return validatePath() & validateIntervalDays() & validateIntervalHours();
    }

    private boolean validatePath() {

        boolean valid = true;

        if (source == null || source.length() <  3){
            this.transientProperties.getMessageList().addMessage("Quellpfadeingabe ist zu kurz");
            valid = false;
        }

        if (target == null || target.length() <  3){
            this.transientProperties.getMessageList().addMessage("Zielpfadeingabe ist zu kurz");
            valid = false;
        }

        if(!valid){
            return valid;
        }

        File sourceDir = new File(source);
        File targetDir = new File(target);

        if (!sourceDir.exists()) {
            System.out.println("Source: " + sourceDir);
            this.transientProperties.getMessageList().addMessage("SOURCE DIRECTORY not EXISTS.");
            valid = false;
        }

        if (!targetDir.exists()) {
            System.out.println("Target" + targetDir);
            this.transientProperties.getMessageList().addMessage("TARGET DIRECTORY not EXISTS.");
            valid = false;
        }

        return valid;
    }

    private boolean validateIntervalHours(){
        if(intervalHours < 0){
            this.transientProperties.getMessageList().addMessage("interval HOURS has to be  non NEGATIVE.");
            return false;
        }
        return true;
    }

    private boolean validateIntervalDays(){
        if(intervalHours < 0) {
            this.transientProperties.getMessageList().addMessage("interval DAYS has to be non NEGATIVE.");
            return false;
        }
        return true;
    }

}


