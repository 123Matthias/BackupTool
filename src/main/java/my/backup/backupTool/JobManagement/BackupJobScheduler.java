package my.backup.backupTool.JobManagement;

import my.backup.backupTool.DataRepository.BaseDataRepository;
import my.backup.backupTool.DataRepository.IStoreData;
import my.backup.backupTool.Model.BackupType;
import my.backup.backupTool.Model.IModel;
import my.backup.backupTool.Service.IMergeService;
import my.backup.backupTool.Service.MergeService;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BackupJobScheduler implements Subjekt {

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this); // Listener-Verwaltung
    List<IModel> models;
    List<IModel> backupOrderList;
    IStoreData dataStore;

    public BackupJobScheduler() {
        dataStore = new BaseDataRepository();

        try {
            this.models = dataStore.getAllAsList();
        } catch (IOException e) {
            this.models = new ArrayList<>();
        }
        this.backupOrderList = createBackupList();
        this.fireBackupEvent();
    }
    @Override
    public void add(IModel model) {
        models.add(model);
    }

    @Override
    public void remove(IModel model) {
        for(IModel modelInList : models) {
            if(modelInList.getUid().equals(model.getUid()))
                models.remove(modelInList);
        }
    }

    @Override
    public void fireBackupEvent() {
        for(IModel modelInList : models) {
            if(modelInList.hasPlayBackupOrder()){
                if(modelInList.getBackupType() == BackupType.MERGE){
                    IMergeService mergeService = new MergeService(modelInList);
                    mergeService.startMergeThread();
                }
                if(modelInList.getBackupType() == BackupType.FULL){
                    //TODO
                }
                if(modelInList.getBackupType() == BackupType.SYNCHRONIZED){
                    //TODO
                }
            }
        }
    }


    public void addChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removeChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void fireProgressStateBackup(double progressState) {
        for(IModel model : backupOrderList) {
                double oldProgressState = model.getProgressStateProp();
                model.setProgressStateProp(progressState);
                propertyChangeSupport.firePropertyChange("progressState", oldProgressState, progressState);
        }
    }

    public List<IModel> createBackupList(){
        List<IModel> backupList = new ArrayList<>();
        for(IModel model : this.models) {
            if (model.hasPlayBackupOrder()) {
                backupList.add(model);
            }
        }
        return backupList;
    }

    public void removeBackupOrderFromList(IModel model) {
        for(int i = 0; i < backupOrderList.size(); i++) {
            if(backupOrderList.get(i).getUid().equals(model.getUid())) {
                this.backupOrderList.remove(i);
                break;
            }
        }
    }

    public void addBackupOrderToList(IModel model) {
        this.backupOrderList.add(model);
    }

}
