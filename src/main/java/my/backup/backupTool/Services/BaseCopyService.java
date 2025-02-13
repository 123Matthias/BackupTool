package my.backup.backupTool.Services;

import my.backup.backupTool.Model.BaseModel;

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
            long fileProcessedSize = 0;

            //END OF Encryption Service usage
            while ((bytesRead = in.read(buffer)) != -1) {

                out.write(buffer, 0, bytesRead);
                fileProcessedSize += bytesRead;

                // Fortschritt berechnen
                long progress = fileProcessedSize / totalSize;
                super.updateProgressBar(this.model);

             //   super.calculateWorkingSpeed(fileProcessedSize,this.model);
            }

        } catch(IOException e) {
            super.messageList.addMessage(e.getMessage());
        }
    }

    public void copyFileWithFileChannel(Path sourceFile, Path targetFile, long totalSize) {
        super.setLastProcessedSize(0);
        try (FileChannel sourceChannel = FileChannel.open(sourceFile, StandardOpenOption.READ);
             FileChannel targetChannel = FileChannel.open(targetFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

            long fileProcessedSize = 0;
            long transferBufferSize = 64*1024; // 64k Buffer initial
            long fileSize = sourceChannel.size();
            //If File size greater than 1MB
            if(5*1024*1024 < fileSize){
               transferBufferSize = calculateBufferSize(fileSize);
            //   System.out.println("transferBufferSize: " + transferBufferSize + " fileSize: " + fileSize);
            }

            while (fileProcessedSize < fileSize) {

                long bytesTransferred = sourceChannel.transferTo(fileProcessedSize, transferBufferSize, targetChannel);
                fileProcessedSize += bytesTransferred;
                super.addFileProcessedSize(bytesTransferred);
                super.updateProgressBar(this.model);
                super.calculateWorkingSpeed(this.model);
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Thread interrupted: " + Thread.currentThread().getName());
                    return;
                }
            }




        } catch (IOException e) {
            super.messageList.addMessage(e.getMessage());
        }
    }


    private long calculateBufferSize(long fileSize) {
        if (fileSize < 50 * 1024 * 1024) { // Dateien unter 50 MB
            return 128 * 1024; // 128 KB
        } else if (fileSize < 100 * 1024 * 1024) { // 50 MB - 100 MB
            return 256 * 1024; // 256 KB
        } else if (fileSize < 1024 * 1024 * 1024) { // Dateien unter 1 GB
            return 1024 * 1024; // 1 MB
        } else { // Sehr große Dateien über 1 GB
            return 2 * 1024 * 1024; // 2 MB
        }
    }

    public BaseModel getModel() {
        return model;
    }
}
