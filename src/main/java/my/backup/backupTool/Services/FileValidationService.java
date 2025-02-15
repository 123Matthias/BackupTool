package my.backup.backupTool.Services;

import javafx.application.Platform;
import my.backup.backupTool.App;
import my.backup.backupTool.JobManagement.Hardware;
import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.Notifications.IMessageList;
import my.backup.backupTool.Notifications.MessageList;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.CRC32;


public class FileValidationService extends BaseCalculationService implements IFileValidationService {

    private long totalFileSize;
    private long progress;
    private IMessageList messageList;
    private int checkedFiles;
    AtomicInteger fileTreeVisited = new AtomicInteger(0);
    private BaseModel model;
    private Hardware hardware;
    CRC32ConcatWrapper crc32Wrapper = new CRC32ConcatWrapper();
    List<CRC32> crc32List = new ArrayList<>();

    public FileValidationService(BaseModel model) {
        messageList = new MessageList();
        this.model = model;
        super.setTotalFileSize(super.calculateTotalSize(Paths.get(this.model.getSource()))*2);
        this.hardware = Hardware.getHardwareInfo();

    }


    public byte[] calculateHash(Path file, String algorithm) {
        MessageDigest digest = null;

        try {
            digest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        try (InputStream is = Files.newInputStream(file)) {

            byte[] buffer = new byte[8192]; // 8 KB Puffer
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        byte[] hashBytes = digest.digest();
        return hashBytes;
    }

    public byte[] concatHash(byte[] oldHash, byte[] newHash) {
        MessageDigest digest = null;

        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        digest.update(oldHash);
        digest.update(newHash);

        // Gib den kombinierten Hash zurück
        return digest.digest();
    }

    // Hilfsmethode, um einen long-Wert in ein Byte-Array umzuwandeln
    private byte[] longToByteArray(long value) {
        byte[] byteArray = new byte[8]; // Ein long-Wert besteht aus 8 Bytes
        byteArray[0] = (byte) (value >>> 56);
        byteArray[1] = (byte) (value >>> 48);
        byteArray[2] = (byte) (value >>> 40);
        byteArray[3] = (byte) (value >>> 32);
        byteArray[4] = (byte) (value >>> 24);
        byteArray[5] = (byte) (value >>> 16);
        byteArray[6] = (byte) (value >>> 8);
        byteArray[7] = (byte) (value);
        return byteArray;
    }

    protected CRC32 calculateCRC32WithFileChannel(Path sourceFile) {
        CRC32 crc32 = new CRC32();

        try (FileChannel inputChannel = FileChannel.open(sourceFile, StandardOpenOption.READ)) {

            ByteBuffer buffer;

            if (inputChannel.size() < 5 * 1024 * 1024) {
                buffer = ByteBuffer.allocateDirect((int) super.DEFAULT_BUFFERSIZE);
            } else {
                buffer = ByteBuffer.allocate((int) super.calculateBufferSize(inputChannel.size()));
            }


            while (inputChannel.read(buffer) > -1) {
                buffer.flip();
                int bytesRead = buffer.remaining();
                crc32.update(buffer);
                super.addFileProcessedSize(bytesRead);
                buffer.clear();

                if (inputChannel.size() < 50 * 1024 * 1024) {
                    continue;
                } else {
                    System.out.println("File Processed Size= " + super.getSumFileProcessedSize());
                    System.out.println("TotalFileSize= " + super.getTotalFileSize());
                    super.updateProgressBar(this.model);
                    super.calculateWorkingSpeed(this.model);
                }
            }

            super.updateProgressBar(this.model);
            super.calculateWorkingSpeed(this.model);

        } catch (IOException e) {
            super.messageList.addMessage(e.getMessage());
        }

        return crc32;
    }

    public String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public long checkAllFilesCRC32(String source, String target) {
        Path sourcePath = Paths.get(source);
        Path targetPath = Paths.get(target);

        long checked = 0;
        try {
            this.totalFileSize = super.calculateTotalSize(sourcePath);
            checked = calculateCRC32FromPath(sourcePath, targetPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return checked;

    }

    private long calculateCRC32FromPath(Path sourcePath, Path targetPath) throws IOException {
        ExecutorService executor = Executors.newFixedThreadPool(hardware.preferredThreadCount());
        this.checkedFiles = 0;

        // Durchlaufe alle Dateien im Verzeichnis
        Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path source, BasicFileAttributes attrs) {
                fileTreeVisited.incrementAndGet();
                executor.submit(() -> {
                    // Berechne den relativen Pfad zwischen sourcePath und der aktuellen Datei
                    Path relativePath = sourcePath.relativize(source);
                    // Erstelle den Zielpfad
                    Path target = targetPath.resolve(relativePath);

                        CRC32 crc32Source = calculateCRC32WithFileChannel(source);
                        CRC32 crc32Target = calculateCRC32WithFileChannel(target);
                      //  System.out.println("CRC32Source: " + crc32Source.getValue() + " SourcePath: " + source);
                      //  System.out.println("CRC32Target: " + crc32Target.getValue() + " TargetPath: " + target);
                        if (crc32Source.getValue() == crc32Target.getValue()) {
                            addCheckedFile();  // Wenn die CRC32-Werte übereinstimmen, erhöhe den Zähler
                        }

                });
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                System.err.println("Fehler beim Besuchen der Datei: " + file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                if (exc != null) {
                    System.err.println("Fehler beim Besuchen des Verzeichnisses: " + dir);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        // Warten auf den Abschluss aller Aufgaben
        executor.shutdown();  // Startet das Herunterfahren des Executors
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();  // Zwingt den Executor zum sofortigen Herunterfahren
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Executor wurde nach 120 Sekunden nicht ordnungsgemäß beendet");
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();  // Interrupt-Flag zurücksetzen
        }

        // Rückgabe des Zählers für die überprüften Dateien
        return checkedFiles;
    }

    private synchronized void addCheckedFile() {
        this.checkedFiles++;  // Synchronisierte Methode zum sicheren Inkrementieren des Zählers
    }


    public synchronized boolean validate(){
        if(this.model.getSourceValidationValue().equals(this.model.getTargetValidationValue())){
            return true;
        }
        else {
            return false;
        }
    }

    public boolean calculateAndSaveCRC32Validation() {
        System.out.println("Starting Validation Service");

        super.finishCalculations(this.model);
        long crc32Source = checkAllFilesCRC32(this.model.getSource(), this.model.getTarget());
        super.finishCalculations(this.model);
        Platform.runLater(() -> {
            this.model.setSourceValidationValue(String.valueOf(crc32Source));
            this.model.setTargetValidationValue(String.valueOf(fileTreeVisited));
            validate();
        });


        this.model.setValidationJob(false);
        super.finishCalculations(this.model);


        App.DataStore.saveModelAsJSON(this.model);

        System.out.println("SOURCE HASH IS " + this.model.getSourceValidationValue());
        System.out.println("TARGET HASH IS " + this.model.getTargetValidationValue());

        return !this.model.getSourceValidationValue().isEmpty() && !this.model.getTargetValidationValue().isEmpty();

    }
}

