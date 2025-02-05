package my.backup.backupTool.Service;

import my.backup.backupTool.Encryption.AesService;
import my.backup.backupTool.Model.BaseModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.*;

public abstract class BaseCopyService extends BaseCalculationService {

    private final BaseModel model;


    public BaseCopyService(BaseModel model) {
        this.model = model;
        System.out.println("Model BaseCopyService: " + this.model);
    }


    protected void copyFileWithStream(Path sourceFile, Path targetFile, long totalSize) {

        try (InputStream in = Files.newInputStream(sourceFile);
             OutputStream out = Files.newOutputStream(targetFile,
                     StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            double fileProcessedSize = 0;

            //END OF Encryption Service usage
            while ((bytesRead = in.read(buffer)) != -1) {
                long iterationStartTime = System.nanoTime();

                out.write(buffer, 0, bytesRead);
                fileProcessedSize += bytesRead;

                // Fortschritt berechnen
                double progress = fileProcessedSize / totalSize;
                super.updateProgress(progress, this.model);

                long iterationEndTime = System.nanoTime();
                super.calculateWorkingSpeed(fileProcessedSize,totalSize,this.model);
            }

        } catch(IOException e) {
            super.messageList.addMessage(e.getMessage());
        }
    }

    protected void copyFileWithFileChannel(Path sourceFile, Path targetFile, long totalSize) {
        try (FileChannel sourceChannel = FileChannel.open(sourceFile, StandardOpenOption.READ);
             FileChannel targetChannel = FileChannel.open(targetFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

            long fileProcessedSize = 0;
            long transferChunkSize = 16 * 1024 * 1024; // 16 MB Blockgröße
            long fileSize = sourceChannel.size();

            while (fileProcessedSize < fileSize) {

                // Kopiere einen Chunk der Datei von source to target
                long bytesTransferred = sourceChannel.transferTo(fileProcessedSize, transferChunkSize, targetChannel);

                fileProcessedSize += bytesTransferred;

                double progress = (double) fileProcessedSize / totalSize;

                super.updateProgress(progress, this.model);
            }

        } catch (IOException e) {
            super.messageList.addMessage(e.getMessage());
        }
    }

    protected BaseModel getModel() {
        return this.model;
    }

}
