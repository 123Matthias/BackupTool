package my.backup.backupTool.Service;

import my.backup.backupTool.Controller.IMessageController;
import my.backup.backupTool.Controller.MessageController;
import my.backup.backupTool.Model.IModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class MergeService implements IMergeService {


    IMessageController messageController;

    public MergeService() {

        messageController = new MessageController();

    }

    @Override
    public void startMergeData(IModel model) {
        File sourceDir = new File(model.getSource());
        File targetDir = new File(model.getTarget());
        String sourceDisk = sourceDir.toString().substring(0,2);
        String targetDisk = sourceDir.toString().substring(0,2);


        if(model.validate() == false){
             messageController.show(model.getMessageList());
            return;
        }



        try {
            Files.walkFileTree(Paths.get(model.getSource()), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    // Deine Logik für Verzeichnisse
                    Path targetDirPath = Paths.get(model.getTarget(), dir.toString().substring(model.getSource().length()));
                    // Zielverzeichnis erstellen, falls es noch nicht existiert
                    if (!Files.exists(targetDirPath)) {
                        Files.createDirectories(targetDirPath);
                        System.out.println("Verzeichnis erstellt: " + targetDirPath);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                    Path targetFilePath = Paths.get(model.getTarget(), file.toString().substring(model.getSource().length()));

                    //check Substring and if exists check the modified date. targetFilePath is used for this check
                    if (Files.exists(targetFilePath)) {
                        long targetLastModifiedTime = Files.getLastModifiedTime(targetFilePath).toMillis();
                        long sourceLastModifiedTime = Files.getLastModifiedTime(file).toMillis();

                        //Here the Changes are copied
                        if (sourceLastModifiedTime > targetLastModifiedTime) {
                            Files.copy(file, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("Datei ersetzt (neuer): " + file);
                        } else {
                            System.out.println("Datei übersprungen (älter oder gleich): " + file);
                        }
                    } else {
                        // Wenn die Ziel-Datei noch nicht existiert, einfach kopieren
                        Files.copy(file, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("Datei kopiert: " + file);
                    }
                    return FileVisitResult.CONTINUE;
                }

                //TODO exception handling
                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    System.err.println("Fehler beim Besuchen der Datei: " + file);
                    exc.printStackTrace();
                    return FileVisitResult.CONTINUE;
                }

                //TODO exception handling
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    // Falls ein Fehler beim Besuch eines Verzeichnisses auftritt
                    if (exc != null) {
                        System.err.println("Fehler beim Besuchen des Verzeichnisses: " + dir);
                        exc.printStackTrace();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
            //TODO exception handling
        } catch (IOException e) {
            System.err.println("Fehler beim Durchsuchen des Verzeichnisses: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
