package my.backup.backupTool.Service;


import javafx.application.Platform;
import my.backup.backupTool.App;
import my.backup.backupTool.Factory.CopyServiceFactory;
import my.backup.backupTool.Controller.MessageTYPE;
import my.backup.backupTool.Model.BaseModel;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;

public class MergeService implements IMergeService,Runnable {

    private ICopyService copyService;
    private IMessageList messageList;
    private volatile BaseModel model;
    Thread thread;
    public MergeService(BaseModel model) {
        this.copyService = CopyServiceFactory.createCopyService(model);
        this.model = model;
        this.messageList = new MessageList();
    }

    @Override
    public void startMergeThread() {
        this.thread = new Thread(this);
        this.thread.start();
    }


    @Override
    public void run() {

        System.out.println("Thread STARTED: " + Thread.currentThread().getName());


        if (!this.model.validate()) {
            Platform.runLater(() -> MessageService.createMessage(this.model.TransientProperties.getMessageList(), MessageTYPE.VALIDATION));
            return;
        }

        long totalSize;

        try {
            totalSize = this.copyService.calculateTotalSize(Paths.get(this.model.getSource()));
            copyFileTree(this.copyService, totalSize);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.copyService.finishCalculations(this.model);

        LocalDateTime nextBackupTime = App.JobScheduler.calculateNextBackupTime(this.model);
        this.model.setNextBackupLocalDateTime(nextBackupTime);
        //Last Backup Time muss unbedingt nach next Backup time stehn im Zeitlichen Verlauf
        LocalDateTime lastBackupTime = LocalDateTime.now();
        this.model.setLastBackupLocalDateTime(lastBackupTime);


        if(model.isRestoreMode()){
            model.setRestoreMode(false);
        }

        App.DataStore.saveModelAsJSON(this.model);
        App.JobScheduler.backupThreadFinished(Thread.currentThread());

      //  App.JobScheduler.backupThreadFinished(Thread.currentThread());
        System.out.println("Thread BEENDET: " + Thread.currentThread().getName());
    }

    private void copyFileTree(ICopyService copyService, long totalFileSize) throws IOException {

        Files.walkFileTree(Paths.get(this.model.getSource()), new SimpleFileVisitor<Path>() {

            private final String targetString = MergeService.this.model.getTarget();
            private final int subDirectoryStartpoint = MergeService.this.model.getSource().length();

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
                //StopAndInterruptButtonClick
                if (Thread.interrupted()) {
                    return FileVisitResult.TERMINATE;
                }

                Path targetFilePath = Paths.get(targetString, file.toString().substring(subDirectoryStartpoint));

                if (Files.exists(targetFilePath)) {
                    long targetLastModifiedTime = Files.getLastModifiedTime(targetFilePath).toMillis();
                    long sourceLastModifiedTime = Files.getLastModifiedTime(file).toMillis();
                    double targetFileSize = Files.size(targetFilePath);
                    double sourceFileSize = Files.size(file);

                    if (sourceLastModifiedTime > targetLastModifiedTime || sourceFileSize != targetFileSize) {
                        copyService.copyFileWithFileChannel(file, targetFilePath, totalFileSize);
                        //           System.out.println("Datei ersetzt (neuer): " + file);
                    }
                }

                else {
                    copyService.copyFileWithFileChannel(file, targetFilePath, totalFileSize);
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

    public Thread getThread() {
        return thread;
    }


}
