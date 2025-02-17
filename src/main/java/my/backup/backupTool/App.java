package my.backup.backupTool;

import my.backup.backupTool.DataRepository.BaseDataStoreRepository;
import my.backup.backupTool.DataRepository.IDataStore;
import my.backup.backupTool.JobManagement.BackupJobScheduler;
import my.backup.backupTool.JobManagement.BackupValidationScheduler;
import my.backup.backupTool.JobManagement.Hardware;
import my.backup.backupTool.JobManagement.JobTimeline;


public class App {
    public static IDataStore DataStore;
    public static Router Router;
    public static BackupJobScheduler JobScheduler;
    public static BackupValidationScheduler ValidationScheduler;
    public static Hardware Hardware;

    public App() {
        Hardware = my.backup.backupTool.JobManagement.Hardware.getHardwareInfo();
        DataStore = BaseDataStoreRepository.Singleton();
        JobScheduler = BackupJobScheduler.Singleton();
        ValidationScheduler = BackupValidationScheduler.Singleton();
        Router = my.backup.backupTool.Router.Singleton();
    }
}
