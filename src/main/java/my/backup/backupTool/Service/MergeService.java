package my.backup.backupTool.Service;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import my.backup.backupTool.Controller.MessageController;
import my.backup.backupTool.MessageTYPE;
import my.backup.backupTool.Model.IModel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MergeService implements IMergeService {



    public MergeService() {

    }


    @Override
    public void startMergeData(IModel model) {
        Runnable mergeTask = new Runnable() {
            @Override
            public void run() {
                File sourceDir = new File(model.getSource());
                File targetDir = new File(model.getTarget());
                String sourceDisk = sourceDir.toString().substring(0, 2);
                String targetDisk = sourceDir.toString().substring(0, 2);

                if (model.validate() == false) {
                    MessageService.createMessage(model.getMessageList(), MessageTYPE.VALIDATION);
                    return;
                }

                long s = 0;
                try {
                    s = calculateTotalSize(Paths.get(model.getSource()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                final long totalSize = s;
                AtomicLong processedSize = new AtomicLong(0);

                try {
                    Files.walkFileTree(Paths.get(model.getSource()), new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                            Path targetDirPath = Paths.get(model.getTarget(), dir.toString().substring(model.getSource().length()));
                            if (!Files.exists(targetDirPath)) {
                                Files.createDirectories(targetDirPath);
                                System.out.println("Verzeichnis erstellt: " + targetDirPath);
                            }
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            Path targetFilePath = Paths.get(model.getTarget(), file.toString().substring(model.getSource().length()));

                            if (Files.exists(targetFilePath)) {
                                long targetLastModifiedTime = Files.getLastModifiedTime(targetFilePath).toMillis();
                                long sourceLastModifiedTime = Files.getLastModifiedTime(file).toMillis();

                                if (sourceLastModifiedTime > targetLastModifiedTime) {
                                    copyFileWithProgress(file, targetFilePath, processedSize, totalSize, model);
                                    System.out.println("Datei ersetzt (neuer): " + file);
                                } else {
                                    System.out.println("Datei übersprungen (älter oder gleich): " + file);
                                }
                            } else {
                                copyFileWithProgress(file, targetFilePath, processedSize, totalSize, model);
                                System.out.println("Datei kopiert: " + file);
                            }

                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                            System.err.println("Fehler beim Besuchen der Datei: " + file);
                            exc.printStackTrace();
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                            if (exc != null) {
                                System.err.println("Fehler beim Besuchen des Verzeichnisses: " + dir);
                                exc.printStackTrace();
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    });
                } catch (IOException e) {
                    System.err.println("Fehler beim Durchsuchen des Verzeichnisses: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        System.out.println("----------------Starting merge task in Thread-------------");
        new Thread(mergeTask).start();
    }

    private void copyFileWithProgress(Path sourceFile, Path targetFile, AtomicLong processedSize, long totalSize, IModel model) throws IOException {
        try (InputStream in = Files.newInputStream(sourceFile);
             OutputStream out = Files.newOutputStream(targetFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            byte[] buffer = new byte[1024 * 1024]; // 1MB Blockgröße
            int bytesRead;
            long fileProcessedSize = 0;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                fileProcessedSize += bytesRead;
                processedSize.addAndGet(bytesRead);

                // Fortschritt berechnen
                double progress = (double) processedSize.get() / totalSize;

                System.out.println("Processed size: " + processedSize.get());
                System.out.println("Progress: " + progress);

                // Update progress auf der UI (in der JavaFX-Thread)
                Platform.runLater(() -> updateProgress(progress, model));
            }
        }
    }

    private void updateProgress(double progress, IModel model) {
        // Hier wird der Fortschritt an die UI übergeben
        // Je nach deinem UI-Framework (z.B. JavaFX) wird die ProgressBar oder ein anderes UI-Element aktualisiert
        model.setProgressState(progress); // Beispiel, falls dein Model eine Methode setProgress() hat
        System.out.println("Aktueller Fortschritt: " + (int) (model.getProgressState() * 100) + "%");
    }

    private long calculateTotalSize(Path sourcePath) throws IOException {
        final AtomicLong totalSize = new AtomicLong(0);

        Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                // Add the file size to the total size
                totalSize.addAndGet(attrs.size());
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                // Handle failure if needed
                return FileVisitResult.CONTINUE;
            }
        });

        return totalSize.get();
    }

}
