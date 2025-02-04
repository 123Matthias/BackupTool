package my.backup.backupTool.Service;


import javafx.application.Platform;
import my.backup.backupTool.App;
import my.backup.backupTool.MessageTYPE;
import my.backup.backupTool.Model.BaseModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;

public class MergeService extends BaseCopyService implements IMergeService,Runnable {


    private IMessageList messageList;

    public MergeService(BaseModel model) {
        super(model);
        this.messageList = new MessageList();
    }

    @Override
    public void startMergeThread() {
        Thread thread = new Thread(this);
        thread.start();
    }


    @Override
    public void run() {

        System.out.println("Thread STARTED: " + Thread.currentThread().getName());

        File sourceDir = new File(super.getModel().getSource());
        File targetDir = new File(super.getModel().getTarget());

        if (!super.getModel().validate()) {
            Platform.runLater(() -> MessageService.createMessage(super.getModel().getMessageList(), MessageTYPE.VALIDATION));
            return;
        }

        long totalSize;

        try {
            totalSize = super.calculateTotalSize(Paths.get(super.getModel().getSource()));
            copyFileTree(totalSize);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        super.updateProgress(0.0, super.getModel());
        LocalDateTime lastBackupTime = TimeService.calculateLastBackupTime(LocalDateTime.now());
        LocalDateTime nextBackupTime = TimeService.calculateNextBackupTime(super.getModel().getLastBackupLocalDateTime(),super.getModel().getIntervalDays(), super.getModel().getIntervalHours());
        super.getModel().setNextBackupLocalDateTime(nextBackupTime);
        super.getModel().setLastBackupLocalDateTime(lastBackupTime);

        System.out.println("Thread BEENDET: " + Thread.currentThread().getName());
    }

    private void copyFileTree(long totalFileSize) throws IOException {

        Files.walkFileTree(Paths.get(super.getModel().getSource()), new SimpleFileVisitor<Path>() {

            private final String targetString = MergeService.super.getModel().getTarget();
            private final int subDirectoryStartpoint = MergeService.super.getModel().getSource().length();

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path targetDirPath = Paths.get(targetString, dir.toString().substring(subDirectoryStartpoint));
                if (!Files.exists(targetDirPath)) {
                    Files.createDirectories(targetDirPath);
                    //System.out.println("Verzeichnis erstellt: " + targetDirPath);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                Path targetFilePath = Paths.get(targetString, file.toString().substring(subDirectoryStartpoint));

                if (Files.exists(targetFilePath)) {
                    long targetLastModifiedTime = Files.getLastModifiedTime(targetFilePath).toMillis();
                    long sourceLastModifiedTime = Files.getLastModifiedTime(file).toMillis();

                    if (sourceLastModifiedTime > targetLastModifiedTime) {
                        MergeService.super.copyFileWithStream(file, targetFilePath, totalFileSize);
                        //           System.out.println("Datei ersetzt (neuer): " + file);
                    }
                }

                else {
                    MergeService.super.copyFileWithStream(file, targetFilePath, totalFileSize);
                    //System.out.println("Datei kopiert: " + file);
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

    }

}
