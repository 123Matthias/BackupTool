package my.backup.backupTool.Services;


public interface IMergeService {
    void startMergeThread();
    Thread getThread();
}
