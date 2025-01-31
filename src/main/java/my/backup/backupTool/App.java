package my.backup.backupTool;

import my.backup.backupTool.DataRepository.BaseDataStoreRepository;
import my.backup.backupTool.DataRepository.IDataStore;
import my.backup.backupTool.JobManagement.BackupJobScheduler;
import my.backup.backupTool.JobManagement.BackupCheckScheduler;


public class App {

    public static Router Router;
    public static BackupJobScheduler JobScheduler;
    public static BackupCheckScheduler CheckScheduler;
    public static IDataStore DataStore;

    public App() {
        DataStore = new BaseDataStoreRepository();
        JobScheduler = new BackupJobScheduler(); //Muss als erstes Initialisiert werden da hier Daten geladen werden
        CheckScheduler = new BackupCheckScheduler();
        Router = new Router();

    }

}
