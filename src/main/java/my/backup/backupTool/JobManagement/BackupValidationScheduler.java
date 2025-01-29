package my.backup.backupTool.JobManagement;

import my.backup.backupTool.DataRepository.BaseDataRepository;
import my.backup.backupTool.DataRepository.IStoreData;
import my.backup.backupTool.Model.IModel;
import my.backup.backupTool.Model.HashTYPE;
import my.backup.backupTool.Service.FileValidationService;
import my.backup.backupTool.Service.IFileValidationService;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BackupValidationScheduler {

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this); // Listener-Verwaltung
    List<IModel> models;
    IStoreData dataStore;
    List<IModel> validationOrderList;

    public BackupValidationScheduler() {
        dataStore = new BaseDataRepository();

        try {
            this.models = dataStore.getAllAsList();
        } catch (IOException e) {
            this.models = new ArrayList<>();
        }
        this.validationOrderList = createValidationList();
    }

    public void add(IModel model) {
        models.add(model);
    }


    public void remove(IModel model) {
        for(IModel modelInList : models) {
            if(modelInList.getUid().equals(model.getUid()))
                models.remove(modelInList);
        }
    }


    public void fireValidationEvent() {
        for(IModel modelInList : models) {
            if(modelInList.hasHashOrder()){
                if(modelInList.getHashType() == HashTYPE.CRC32){
                    IFileValidationService hashService = new FileValidationService(modelInList);
                    hashService.calculateAndSaveHashes();
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

    public void fireProgressStateValidation(double progressState) {
        for(IModel model : validationOrderList) {
            double oldProgressState = model.getProgressState();
            model.setProgressState(progressState);
            propertyChangeSupport.firePropertyChange("progressState", oldProgressState, progressState);
        }
    }

    public List<IModel> createValidationList(){
        List<IModel> backupList = new ArrayList<>();
        for(IModel model : this.models) {
            if (model.hasHashOrder()) {
                validationOrderList.add(model);
            }
        }
        return backupList;
    }

    public void removeValidationOrderFromList(IModel model) {
        for(int i = 0; i < validationOrderList.size(); i++) {
            if(validationOrderList.get(i).getUid().equals(model.getUid())) {
                this.validationOrderList.remove(i);
                break;
            }
        }
    }

    public void addValidationOrderToList(IModel model) {
        this.validationOrderList.add(model);
    }



}
