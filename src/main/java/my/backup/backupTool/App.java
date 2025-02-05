package my.backup.backupTool;

import my.backup.backupTool.DataRepository.BaseDataStoreRepository;
import my.backup.backupTool.DataRepository.IDataStore;
import my.backup.backupTool.JobManagement.BackupJobScheduler;
import my.backup.backupTool.JobManagement.BackupValidationScheduler;


public class App {

    public static Router Router;
    public static BackupJobScheduler JobScheduler;
    public static BackupValidationScheduler ValidationScheduler;
    public static IDataStore DataStore;

    public App() {
        DataStore = new BaseDataStoreRepository();
        JobScheduler = new BackupJobScheduler();
        ValidationScheduler = new BackupValidationScheduler();
        Router = new Router();
    }
}
