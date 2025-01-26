package my.backup.backupTool.Service;


import javafx.application.Platform;
import my.backup.backupTool.DataRepository.BaseDataRepository;
import my.backup.backupTool.DataRepository.IStoreData;
import my.backup.backupTool.MessageTYPE;
import my.backup.backupTool.Model.IModel;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.CRC32;

public class MergeService implements IMergeService,Runnable {
    private final IModel model;
    private IStoreData dataStore;
    private IMessageList messageList;
    private ArrayList<String> fileHashList;
    private final int processors = Runtime.getRuntime().availableProcessors();
    private double lastProgressState;
    private byte[] sourceHash = new byte[8096];
    private byte[] targetHash = new byte[8096];

    public MergeService(IModel model) {
        this.messageList = new MessageList();
        this.model = model;
        this.dataStore = new BaseDataRepository();
    }

    @Override
    public void startMergeThread() {
        Thread thread = new Thread(this);
        thread.start();
    }


    @Override
    public void run() {

        System.out.println("Thread STARTED: " + Thread.currentThread().getName());

        File sourceDir = new File(model.getSource());
        File targetDir = new File(model.getTarget());
        String sourceDisk = sourceDir.toString().substring(0, 2);
        String targetDisk = targetDir.toString().substring(0, 2);


        if (!model.validate()) {
            MessageService.createMessage(model.getMessageList(), MessageTYPE.VALIDATION);
            return;
        }

        long totalSize;
        try {
            totalSize = calculateTotalSize(Paths.get(model.getSource()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        AtomicLong processedSize = new AtomicLong(0);

        try {
            Files.walkFileTree(Paths.get(model.getSource()), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path targetDirPath = Paths.get(model.getTarget(), dir.toString().substring(model.getSource().length()));
                    if (!Files.exists(targetDirPath)) {
                        Files.createDirectories(targetDirPath);
                  //      System.out.println("Verzeichnis erstellt: " + targetDirPath);
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
                 //           System.out.println("Datei ersetzt (neuer): " + file);
                        } else {
                      //      System.out.println("Datei übersprungen (älter oder gleich): " + file);
               //             System.out.println("SourceTime: " + sourceLastModifiedTime + " / Target" + targetLastModifiedTime);
                        }
                    } else {
                        copyFileWithProgress(file, targetFilePath, processedSize, totalSize, model);
               //         System.out.println("Datei kopiert: " + file);
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc)  {
                    System.err.println("Fehler beim Besuchen der Datei: " + file);
                    messageList.addMessage(exc.getMessage());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    if (exc != null) {
                        System.err.println("Fehler beim Besuchen des Verzeichnisses: " + dir);
                        messageList.addMessage(exc.getMessage());
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            //      System.err.println("Fehler beim Durchsuchen des Verzeichnisses: " + e.getMessage());
            messageList.addMessage(e.getMessage());
        }


        // Hier fügst du das println für den Thread-Namen ein, wenn der Thread beendet ist
        System.out.println("Thread BEENDET: " + Thread.currentThread().getName());
        model.setProgressState(0.0);
    }



    private synchronized void copyFileWithProgress(Path sourceFile, Path targetFile, AtomicLong processedSize, long totalSize, IModel model) {

        try (InputStream in = Files.newInputStream(sourceFile);
            OutputStream out = Files.newOutputStream(targetFile,
            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

                    byte[] buffer = new byte[8192]; // 8 KB Blockgröße

                    int bytesRead;
                    long fileProcessedSize = 0;

                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                        fileProcessedSize += bytesRead;
                        processedSize.addAndGet(bytesRead);

                        // Fortschritt berechnen
                        double progress = (double) processedSize.get() / totalSize;

                 //       System.out.println("Processed size: " + processedSize.get());
                //        System.out.println("Progress: " + progress);

                        // Update progress auf der UI (in der JavaFX-Thread)
                        updateProgress(progress);
                    }



            } catch(IOException e) {
                    messageList.addMessage(e.getMessage());
            }
    }

    private synchronized void updateProgress(double progress) {
        // Hier wird der Fortschritt an die UI übergeben
        // Je nach deinem UI-Framework (z.B. JavaFX) wird die ProgressBar oder ein anderes UI-Element aktualisiert

        if(progress > lastProgressState + 0.05 || progress == 1.0 ) {
            lastProgressState = progress;
            Platform.runLater(()->model.setProgressStateProp(progress));
        }

        if(progress ==  1.0){
            System.out.println("Making hashes");
            long hash = FileHashService.calculateHashDirectory(model.getSource());
            model.setSourceHash(String.valueOf(hash));
            long hashBackup = FileHashService.calculateHashDirectory(model.getTarget());
            model.setTargetHash(String.valueOf(hashBackup));

            dataStore.saveModelAsJSON(model);

            System.out.println("SOURCE HASH IS " + model.getSourceHash());
            System.out.println("TARGET HASH IS " + model.getTargetHash());

        }

        System.out.println("Aktueller Fortschritt: " + (int) (model.getProgressStateProp() * 100) + "%");
    }

    private long calculateTotalSize(Path sourcePath) throws IOException {
        final AtomicLong totalSize = new AtomicLong(0);

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




}
