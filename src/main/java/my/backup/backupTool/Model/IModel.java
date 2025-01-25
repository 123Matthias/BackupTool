package my.backup.backupTool.Model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.beans.property.DoubleProperty;
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

    void setProgressState(double progressState);

    double getProgressState();

     void addChangeListener(PropertyChangeListener listener);

     void removeChangeListener(PropertyChangeListener listener);

    public boolean hasPlayBackupOrder();

    public void setPlayBackupOrder(boolean playBackupOrder);


    public double getProgressStateProp();

    public void setProgressStateProp(double progress);

    public DoubleProperty progressStateProperty();

}
