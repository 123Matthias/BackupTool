package my.backup.backupTool.Model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import my.backup.backupTool.Service.IMessageList;

import java.beans.PropertyChangeListener;
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

    boolean validate();
    IMessageList getMessageList();

    int getFlowPanePosition();

    void setFlowPanePosition(int flowPanePosition);

    public int getCardWidth();

    public void setCardWidth(int cardWidth);

    public boolean hasPlayBackupOrder();

    public void setPlayBackupOrder(boolean playBackupOrder);

    @JsonIgnore
    double getProgressStateProp();

    public void setProgressStateProp(double progress);

    @JsonIgnore
    DoubleProperty progressStateProperty();

    public double getProgressState();
    public void setProgressState(double progressState);

    @JsonIgnore
    void addChangeListener(PropertyChangeListener listener);

    @JsonIgnore
    void removeChangeListener(PropertyChangeListener listener);

    public String getTargetHash();

    public void setTargetHash(String targetHash);

    public String getSourceHash();

    public void setSourceHash(String sourceHash);

    void setHashType(String hashType);
    String getHashType();
    void setHashOrder(boolean orderHash);
    boolean hasHashOrder();

}
