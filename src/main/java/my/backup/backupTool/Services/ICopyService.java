package my.backup.backupTool.Services;

import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.Enumerations.CryptoMODE;

import java.nio.file.Path;

public interface ICopyService {
    void copyFileWithFileChannel(Path sourceFile, Path targetFile, CryptoMODE cryptoMode);
    void finishCalculations(BaseModel model);
    long calculateTotalSize(Path path);
    void setTotalFileSize(long totalFileSize);
    void addFileProcessedSize(long fileProcessedSize);
    void updateProgressBar(BaseModel model);
    BaseModel getModel();
}
