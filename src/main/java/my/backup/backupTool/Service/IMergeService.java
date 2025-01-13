package my.backup.backupTool.Service;

import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.Model.IModel;

import java.time.LocalDateTime;

public interface IMergeService {
    void startMergeData(IModel model);
}
