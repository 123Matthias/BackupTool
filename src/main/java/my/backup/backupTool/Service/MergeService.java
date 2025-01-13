package my.backup.backupTool.Service;

import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.Model.IModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class MergeService implements IMergeService {

    public void startMergeData(IModel model) {
        File sourceDir = new File(model.getSource());
        File targetDir = new File(model.getTarget());

        // Überprüfen, ob Quell- und Zielverzeichnisse existieren
        if (!sourceDir.exists() || !targetDir.exists()) {
            System.out.println("Source: " + sourceDir);
            System.out.println("Target" + targetDir);
            System.out.println("Quell- oder Zielordner existieren nicht.");
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
                    // Deine Logik für Dateien
                    Path targetFilePath = Paths.get(model.getTarget(), file.toString().substring(model.getSource().length()));
                    Files.copy(file, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Datei kopiert: " + file);
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
                    // Falls ein Fehler beim Besuch eines Verzeichnisses auftritt
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
}
