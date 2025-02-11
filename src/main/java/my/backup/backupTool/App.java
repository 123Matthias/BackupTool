package my.backup.backupTool;

import my.backup.backupTool.DataRepository.BaseDataStoreRepository;
import my.backup.backupTool.DataRepository.IDataStore;
import my.backup.backupTool.JobManagement.BackupJobScheduler;
import my.backup.backupTool.JobManagement.BackupValidationScheduler;
import my.backup.backupTool.JobManagement.JobTimeline;


public class App {

    public static Router Router;
    public static BackupJobScheduler JobScheduler;
    public static JobTimeline JobTimeline;
    public static BackupValidationScheduler ValidationScheduler;
    public static IDataStore DataStore;

    public App() {
        DataStore = BaseDataStoreRepository.Singleton();
        JobScheduler = BackupJobScheduler.Singleton();
        JobTimeline = my.backup.backupTool.JobManagement.JobTimeline.Singleton();
        ValidationScheduler = BackupValidationScheduler.Singleton();
        Router = my.backup.backupTool.Router.Singleton();
    }

}
