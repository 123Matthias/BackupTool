package my.backup.backupTool.Services;

import javafx.application.Platform;
import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.Notifications.IMessageList;
import my.backup.backupTool.Notifications.MessageList;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicLong;


public abstract class BaseCalculationService {
    private long lastTime = System.nanoTime();
    private double lastProcessedSize = 0;
    IMessageList messageList;
    private double lastProgressStateUpdate;
    private long sumFileProcessedSize;
    private long lastSumFileProcessedSize;
    private long totalFileSize;
    private long fileProcessedSize;
    public final long DEFAULT_BUFFERSIZE = 64*1024; // 64k Buffer initial

    public BaseCalculationService() {
        sumFileProcessedSize = 0;
        lastProgressStateUpdate = 0;
        lastSumFileProcessedSize= 0;
        fileProcessedSize = 0;
        messageList = new MessageList();
    }

    public void finishCalculations(BaseModel model) {
        Platform.runLater(()->{ model.TransientProperties.setProgressState(0.0);
                                model.TransientProperties.setWorkingSpeed(0);
                                });
        this.lastProcessedSize = 0;

    }

    public void addFileProcessedSize(long fileProcessedSize){
        this.sumFileProcessedSize += fileProcessedSize;
    }

    public void updateProgressBar(BaseModel model) {
        double progress = (double) this.sumFileProcessedSize / this.totalFileSize;
        if(progress > lastProgressStateUpdate + 0.025 || progress == 1.0) {
            lastProgressStateUpdate = progress;
            Platform.runLater(()-> model.TransientProperties.setProgressState(progress));

           // System.out.println("Model BaseCalculationService: " + model);
           // System.out.println("Progress_Property: " + model.TransientProperties.getProgressStateProperty());
           // System.out.println("Aktueller Fortschritt: " + (int) (model.getProgressState() * 100) + "%");
        }
    }

    public void calculateWorkingSpeed(BaseModel model) {
        long nextTime = System.nanoTime();
        double elapsedTime = (nextTime - this.lastTime) / 1_000_000_000.0;

        if (elapsedTime < 3) {
            return;
        }
        else{
            this.lastTime = nextTime;
            double workingSpeed = ((sumFileProcessedSize - lastSumFileProcessedSize) / (1024.0 * 1024.0)) / elapsedTime;
            this.lastSumFileProcessedSize = sumFileProcessedSize;
            Platform.runLater(()-> model.TransientProperties.setWorkingSpeed(workingSpeed));
         //   System.out.println("Speed: " + model.TransientProperties.getWorkingSpeed());

        }
    }

    public void setLastProcessedSize(double lastProcessedSize) {
        this.lastProcessedSize = lastProcessedSize;
    }

    public long calculateTotalSize(Path sourcePath) {
        AtomicLong totalSize = new AtomicLong(0);

        try {
            Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    // Add the file size to the total size
                    totalSize.addAndGet(attrs.size());

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    messageList.addMessage(exc.getMessage());
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Total Size: " + totalSize.get());
        return totalSize.get();
    }

    public double getLastProgressStateUpdate() {
        return lastProgressStateUpdate;
    }

    public void setLastProgressStateUpdate(double lastProgressStateUpdate) {
        this.lastProgressStateUpdate = lastProgressStateUpdate;
    }

    public double getTotalFileSize() {
        return totalFileSize;
    }

    public void setTotalFileSize(long totalFileSize) {
        this.totalFileSize = totalFileSize;
    }


    public long calculateBufferSize(long fileSize) {
        if (fileSize < 50 * 1024 * 1024) {
            return 128 * 1024; // 128 KiB
        } else if (fileSize < 100 * 1024 * 1024) {
            return 256 * 1024; // 256 KiB
        } else if (fileSize < 1024 * 1024 * 1024) {
            return 1024 * 1024; // 1 MiB
        } else {
            return 2 * 1024 * 1024; // 2 MiB
        }
    }

}
