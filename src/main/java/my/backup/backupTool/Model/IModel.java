package my.backup.backupTool.Model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import my.backup.backupTool.Service.IMessageList;

import java.time.LocalDateTime;

public interface IModel {

    String getUid();

    void setUid(String uid);
    public BackupType getBackupType();

    public void setBackupType(BackupType backupType);
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

    boolean validate();
    IMessageList getMessageList();

    int getFlowPanePosition();

    void setFlowPanePosition(int flowPanePosition);

    public int getCardWidth();

    public void setCardWidth(int cardWidth);

    public boolean hasPlayBackupOrder();

    public void setPlayBackupOrder(boolean playBackupOrder);


    public void setProgressState(double progress);

    @JsonIgnore
    DoubleProperty getProgressStateProperty();

    public double getProgressState();


    void setHashType(HashTYPE hashType);
    HashTYPE getHashType();
    void setHashOrder(boolean orderHash);
    boolean hasHashOrder();

    public String getSourceHash();

    public String getTargetHash();

    public void setSourceHash(String value);

    public void setTargetHash(String value);

    StringProperty getSourceHashProperty();
    StringProperty getTargetHashProperty();

}
