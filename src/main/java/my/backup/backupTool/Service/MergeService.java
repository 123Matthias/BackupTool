package my.backup.backupTool.Service;


import my.backup.backupTool.MessageTYPE;
import my.backup.backupTool.Model.IModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class MergeService extends BaseCopyService implements IMergeService,Runnable {


    private IMessageList messageList;

    public MergeService(IModel model) {
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
        String sourceDisk = sourceDir.toString().substring(0, 2);
        String targetDisk = targetDir.toString().substring(0, 2);


        if (!super.getModel().validate()) {
            MessageService.createMessage(super.getModel().getMessageList(), MessageTYPE.VALIDATION);
            return;
        }

        long totalSize;
        try {
            totalSize = super.calculateTotalSize(Paths.get(super.getModel().getSource()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        try {
            Files.walkFileTree(Paths.get(super.getModel().getSource()), new SimpleFileVisitor<Path>() {
                String targetString = MergeService.super.getModel().getTarget();
                int subDirectoryStartpoint = MergeService.super.getModel().getSource().length();
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path targetDirPath = Paths.get(targetString, dir.toString().substring(subDirectoryStartpoint));
                    if (!Files.exists(targetDirPath)) {
                        Files.createDirectories(targetDirPath);
                  //      System.out.println("Verzeichnis erstellt: " + targetDirPath);
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
                            MergeService.super.copyFileWithStream(file, targetFilePath, totalSize);
                 //           System.out.println("Datei ersetzt (neuer): " + file);
                        } else {
                      //      System.out.println("Datei übersprungen (älter oder gleich): " + file);
               //             System.out.println("SourceTime: " + sourceLastModifiedTime + " / Target" + targetLastModifiedTime);
                        }
                    } else {
                        copyFileWithStream(file, targetFilePath, totalSize);
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

        super.updateProgress(1.0, super.getModel());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Calculating Hashes");

        //TODO auslagern
        FileHashService fileHashService = new FileHashService(super.getModel());
        System.out.println("Model Merge Service: " + super.getModel());
        fileHashService.calculateAndSaveHashes();

        // Hier fügst du das println für den Thread-Namen ein, wenn der Thread beendet ist
        System.out.println("Thread BEENDET: " + Thread.currentThread().getName());

    }

}
