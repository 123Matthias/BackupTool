package my.backup.backupTool.JobManagement;

import my.backup.backupTool.Model.IModel;

public interface Subjekt {
    void add(IModel model);
    void remove(IModel model);
    void fireBackupEvent();
}
