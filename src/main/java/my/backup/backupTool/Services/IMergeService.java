package my.backup.backupTool.Services;


public interface IMergeService extends Runnable {
    Thread getThread();
    void run();
}
