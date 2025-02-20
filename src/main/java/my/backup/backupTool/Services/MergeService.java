package my.backup.backupTool.Services;


import javafx.application.Platform;
import my.backup.backupTool.App;
import my.backup.backupTool.Factory.CopyServiceFactory;
import my.backup.backupTool.Controller.MessageTYPE;
import my.backup.backupTool.JobManagement.Hardware;
import my.backup.backupTool.Model.BackupType;
import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.Notifications.IMessageList;
import my.backup.backupTool.Notifications.MessageList;
import my.backup.backupTool.Notifications.MessageService;
import my.backup.backupTool.ServiceEncryption.CryptoMode;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MergeService implements IMergeService,Runnable {

    private ICopyService copyService;
    private IFileValidationService validationService;
    private IMessageList messageList;
    private volatile BaseModel model;
    private CryptoMode cryptoMode;
    private Hardware hardware;
    private final ExecutorService executor;
    Thread thread;
    public MergeService(BaseModel model) {
        this.copyService = CopyServiceFactory.createCopyService(model);
        this.validationService = new FileValidationService(model);
        this.model = model;
        this.hardware = Hardware.getHardwareInfo();
        this.executor = Executors.newFixedThreadPool(hardware.preferredThreadCount());
        this.messageList = new MessageList();
        getCryptoMode();
    }

    private void getCryptoMode() {
        if(model.hasEncryptionJob() == false){
            cryptoMode = CryptoMode.NONE;
            System.out.println("Crypto mode is " + cryptoMode);
        }
        else if(model.hasEncryptionJob() && model.isRestoreMode() == false){
            cryptoMode = CryptoMode.ENCRYPTION;
            System.out.println("Crypto mode is " + cryptoMode);
        }
        else if(model.hasEncryptionJob() && model.isRestoreMode()){
            cryptoMode = CryptoMode.DECRYPTION;
            System.out.println("Crypto mode is " + cryptoMode);
        }
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
            copyService.setTotalFileSize(totalSize);
            this.copyFileTree(this.copyService, totalSize);
            executor.shutdown();
            while (!executor.isTerminated()) {
                if(Thread.currentThread().isInterrupted()) {
                    executor.shutdownNow();
                }
                try {
                    executor.awaitTermination(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); //Schalter rücksetzen verhindern Interrupt Flag
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        this.copyService.finishCalculations(this.model);


        /*Validation if Validation is Enabled*/
        if(this.model.hasValidationJob()){
            this.validationService.calculateAndSaveCRC32Validation();
        }
        /*END Validation if Validation is Enabled END*/

        LocalDateTime nextBackupTime = App.JobScheduler.calculateNextBackupTime(this.model);
        this.model.setNextBackupLocalDateTime(nextBackupTime);
        LocalDateTime lastBackupTime = LocalDateTime.now();
        this.model.setLastBackupLocalDateTime(lastBackupTime);

        if(model.isRestoreMode()){
            model.setRestoreMode(false);
        }

        App.DataStore.saveModelAsJSON(this.model);

        String log;
        log = String.format("%-20s%s%n", "Title:", this.model.getTitle());
        log += String.format("%-20s%s%n", "UUID:", this.model.getUid());
        log += String.format("%-20s%s%n", "Source:", this.model.getSource());
        log += String.format("%-20s%s%n", "Target:", this.model.getTarget());
        log += String.format("%-20s%s%n", "lastBackupTime:", this.model.getLastBackupLocalDateTime());
        log += String.format("%-20s%s%n", "nextBackupTime:", this.model.getNextBackupLocalDateTime());
        log += String.format("%-20s%s%n", "sourceValidation:", this.model.getSourceValidationValue());
        log += String.format("%-20s%s%n", "targetValidation:", this.model.getTargetValidationValue());
        log += String.format("%-20s%s%n", "encryptionType:", this.model.getEncryptionType());

        LogFileWriterService.writeLogFile(LocalDateTime.now(),LogLevel.COPY_THREAD_SUCCESS, BackupType.MERGE,log);
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
                    executor.shutdownNow();
                    return FileVisitResult.TERMINATE;
                }

                Path targetFilePath = Paths.get(targetString, file.toString().substring(subDirectoryStartpoint));

                if (Files.exists(targetFilePath)) {
                    long targetLastModifiedTime = Files.getLastModifiedTime(targetFilePath).toMillis();
                    long sourceLastModifiedTime = Files.getLastModifiedTime(file).toMillis();
                    long targetFileSize = Files.size(targetFilePath);
                    long sourceFileSize = Files.size(file);

                    if (sourceLastModifiedTime > targetLastModifiedTime || sourceFileSize != targetFileSize) {

                        executor.submit(() -> {
                            copyService.copyFileWithFileChannel(file, targetFilePath, cryptoMode);
                        });
                    }
                    else {
                        copyService.addFileProcessedSize(sourceFileSize);
                        copyService.updateProgressBar(copyService.getModel());
                    }
                }

                else {
                    executor.submit(() -> {
                        copyService.copyFileWithFileChannel(file, targetFilePath, cryptoMode);
                    });
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
