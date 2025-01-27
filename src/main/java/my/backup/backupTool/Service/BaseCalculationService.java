package my.backup.backupTool.Service;

import javafx.application.Platform;
import my.backup.backupTool.Model.IModel;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicLong;


public abstract class BaseCalculationService {

    IMessageList messageList;
    public BaseCalculationService() {
        messageList = new MessageList();
    }

    private double lastProgressState = 0;

    protected void updateProgress(double progress, IModel model) {
        // Hier wird der Fortschritt an die UI übergeben
        // Je nach deinem UI-Framework (z.B. JavaFX) wird die ProgressBar oder ein anderes UI-Element aktualisiert

        if(progress > lastProgressState + 0.05 || progress == 1.0) {
            lastProgressState = progress;
            Platform.runLater(()->model.setProgressStateProp(progress));
            System.out.println("Progress: " + progress);
            System.out.println("Aktueller Fortschritt: " + (int) (model.getProgressStateProp() * 100) + "%");
        }
    }

    protected long calculateTotalSize(Path sourcePath) throws IOException {
        AtomicLong totalSize = new AtomicLong(0);

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
