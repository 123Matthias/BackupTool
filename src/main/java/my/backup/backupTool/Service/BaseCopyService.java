package my.backup.backupTool.Service;

import my.backup.backupTool.Encryption.AesService;
import my.backup.backupTool.Model.BaseModel;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.*;

public class BaseCopyService extends BaseCalculationService implements ICopyService {

    private final BaseModel model;


    public BaseCopyService(BaseModel model) {
        this.model = model;
    }

    //do not USE this Method. very slow by copying to external Disks.
    protected void copyFileWithStream(Path sourceFile, Path targetFile, long totalSize) {

        try (InputStream in = Files.newInputStream(sourceFile);
             OutputStream out = Files.newOutputStream(targetFile,
                     StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            double fileProcessedSize = 0;

            //END OF Encryption Service usage
            while ((bytesRead = in.read(buffer)) != -1) {

                out.write(buffer, 0, bytesRead);
                fileProcessedSize += bytesRead;

                // Fortschritt berechnen
                double progress = fileProcessedSize / totalSize;
                super.updateProgress(progress, this.model);

                super.calculateWorkingSpeed(fileProcessedSize,this.model);
            }

        } catch(IOException e) {
            super.messageList.addMessage(e.getMessage());
        }
    }

    public void copyFileWithFileChannel(Path sourceFile, Path targetFile, long totalSize) {
        try (FileChannel sourceChannel = FileChannel.open(sourceFile, StandardOpenOption.READ);
             FileChannel targetChannel = FileChannel.open(targetFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

            long fileProcessedSize = 0;
            long transferChunkSize = 64*1024; // 8k Block (small and Many Data) TODO Buffer Size calculator
            long fileSize = sourceChannel.size();

            while (fileProcessedSize < fileSize) {

                long bytesTransferred = sourceChannel.transferTo(fileProcessedSize, transferChunkSize, targetChannel);

                fileProcessedSize += bytesTransferred;

                double progress = (double) fileProcessedSize / totalSize;

                super.updateProgress(progress, this.model);
                super.calculateWorkingSpeed(fileProcessedSize,this.model);

                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Thread interrupted: " + Thread.currentThread().getName());
                    return;
                }
            }

        } catch (IOException e) {
            super.messageList.addMessage(e.getMessage());
        }
    }

    protected BaseModel getModel() {
        return this.model;
    }

}
