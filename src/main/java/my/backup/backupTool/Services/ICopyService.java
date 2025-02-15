package my.backup.backupTool.Services;

import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.ServiceEncryption.CryptoMode;

import java.nio.file.Path;

public interface ICopyService {
    void copyFileWithFileChannel(Path sourceFile, Path targetFile, CryptoMode cryptoMode);
    void finishCalculations(BaseModel model);
    long calculateTotalSize(Path path);
    void setTotalFileSize(long totalFileSize);
    void addFileProcessedSize(long fileProcessedSize);
    void updateProgressBar(BaseModel model);
    BaseModel getModel();
}
