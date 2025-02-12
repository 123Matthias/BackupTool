package my.backup.backupTool.CopyServices;


public interface IMergeService {
    void startMergeThread();
    Thread getThread();
}
