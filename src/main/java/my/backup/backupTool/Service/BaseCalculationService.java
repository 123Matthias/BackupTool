package my.backup.backupTool.Service;

import javafx.application.Platform;
import my.backup.backupTool.Model.BaseModel;

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
    public BaseCalculationService() {
        messageList = new MessageList();
    }
    private double lastProgressState = 0;



    public void finishCalculations(BaseModel model) {
        Platform.runLater(()->{ model.setProgressState(0.0);
                                model.setWorkingSpeed(0);
                                });
        this.lastProcessedSize = 0;

    }

    protected void updateProgress(double progress, BaseModel model) {
        if(progress == 0.0 || progress > lastProgressState + 0.05 || progress == 1.0) {
            lastProgressState = progress;
            Platform.runLater(()-> model.setProgressState(progress));

           // System.out.println("Model BaseCalculationService: " + model);
           // System.out.println("Progress_Property: " + model.getProgressStateProperty());
           // System.out.println("Aktueller Fortschritt: " + (int) (model.getProgressState() * 100) + "%");
        }
    }


    protected void calculateWorkingSpeed(double fileProcessedSize, BaseModel model) {
        long nextTime = System.nanoTime();
        double elapsedTime = (nextTime - this.lastTime) / 1_000_000_000.0;

        if (elapsedTime < 3) {
            return;
        }
        else{
            this.lastTime = nextTime;
            double workingSpeed = ((fileProcessedSize - lastProcessedSize) / (1024.0 * 1024.0)) / elapsedTime;
            this.lastProcessedSize = fileProcessedSize;
            Platform.runLater(()-> model.setWorkingSpeed(workingSpeed));

        }

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

    public double getLastProgressState() {
        return lastProgressState;
    }

    public void setLastProgressState(double lastProgressState) {
        this.lastProgressState = lastProgressState;
    }
}
