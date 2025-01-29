package my.backup.backupTool;

import my.backup.backupTool.JobManagement.BackupJobScheduler;


public class App {


    public static Router Router;
    public static BackupJobScheduler JobScheduler;

    public App() {
        Router = new Router();
        JobScheduler = new BackupJobScheduler();
    }

}
