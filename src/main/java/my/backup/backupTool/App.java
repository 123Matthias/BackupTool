package my.backup.backupTool;

import my.backup.backupTool.JobManagement.BackupJobScheduler;


public class App {


    public static Router Router;
    public static BackupJobScheduler JobScheduler;

    public App() {
        JobScheduler = new BackupJobScheduler(); //Muss als erstes Initialisiert werden da hier Daten geladen werden
        Router = new Router();



    }

}
