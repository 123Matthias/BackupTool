package my.backup.backupTool.Services;

import javafx.application.Platform;
import my.backup.backupTool.App;
import my.backup.backupTool.Enumerations.CryptoMODE;
import my.backup.backupTool.Enumerations.LogLEVEL;
import my.backup.backupTool.JobManagement.Hardware;
import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.Enumerations.ValidationTYPE;
import my.backup.backupTool.Notifications.IMessageList;
import my.backup.backupTool.Notifications.MessageList;
import my.backup.backupTool.ServiceEncryption.AesService;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.CRC32;

import static my.backup.backupTool.ServiceEncryption.AesService.initCipherEncryption;


public class FileValidationService extends BaseCalculationService implements IFileValidationService {

    private long totalFileSize;
    private long progress;
    private IMessageList messageList;
    private final AtomicInteger validFilesCount;
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
        this.validFilesCount = new AtomicInteger(0);
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
                buffer = ByteBuffer.allocate((int) super.DEFAULT_BUFFERSIZE);
            } else {
                buffer = ByteBuffer.allocate((int) super.calculateBufferSize(inputChannel.size()));
            }

            while (inputChannel.read(buffer) > -1) {
                buffer.flip();
                int bytesRead = buffer.remaining();
                crc32.update(buffer.array(),0,buffer.limit());
                super.addFileProcessedSize(bytesRead);
                buffer.clear();

                if (inputChannel.size() < 50 * 1024 * 1024) {
                    continue;
                } else {
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

    protected CRC32 calculateEncryptedCRC32WithFileChannel(Path filePath) {
        Cipher cipher = AesService.initCipherEncryption(model.TransientProperties.getSecretKey(), model.TransientProperties.getInitVector());
        CRC32 crc32 = new CRC32();

        try (FileChannel inputChannel = FileChannel.open(filePath, StandardOpenOption.READ)) {
            ByteBuffer buffer;

            if (inputChannel.size() < 5 * 1024 * 1024) {
                buffer = ByteBuffer.allocate((int) super.DEFAULT_BUFFERSIZE);
            } else {
                buffer = ByteBuffer.allocate((int) super.calculateBufferSize(inputChannel.size()));
            }

            while (inputChannel.read(buffer) > -1) {
                buffer.flip();
                byte[] inputData = new byte[buffer.remaining()];
                buffer.get(inputData);
                byte[] encryptedData = cipher.update(inputData);
                crc32.update(encryptedData,0,encryptedData.length);
                super.addFileProcessedSize(encryptedData.length);
                buffer.clear();

                if (inputChannel.size() < 50 * 1024 * 1024) {
                    continue;
                } else {
                    super.updateProgressBar(this.model);
                    super.calculateWorkingSpeed(this.model);
                }
            }

            super.updateProgressBar(this.model);
            super.calculateWorkingSpeed(this.model);

            // Finalen Block entschlüsseln und schreiben
            byte[] finalBlock = cipher.doFinal();
            if (finalBlock != null && finalBlock.length > 0) {
                crc32.update(finalBlock,0,finalBlock.length);
            }

        } catch (IOException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }

        super.updateProgressBar(this.model);
        super.calculateWorkingSpeed(this.model);
        return crc32;
    }


    public String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public int checkAllFilesCRC32(String source, String target, CryptoMODE cryptoMODE) {
        Path sourcePath = Paths.get(source);
        Path targetPath = Paths.get(target);

        int validFilesCount = 0;
        try {
            this.totalFileSize = super.calculateTotalSize(sourcePath);
            validFilesCount = calculateCRC32FromPath(sourcePath, targetPath, cryptoMODE).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return validFilesCount;
    }

    private AtomicInteger calculateCRC32FromPath(Path sourcePath, Path targetPath, CryptoMODE cryptoMODE) throws IOException {
        ExecutorService executor = Executors.newFixedThreadPool(hardware.preferredThreadCount());
        this.validFilesCount.set(0);

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
                    CRC32 crc32Source;
                    CRC32 crc32Target;
                        if(cryptoMODE == CryptoMODE.NONE){
                            crc32Source = calculateCRC32WithFileChannel(source);
                            crc32Target = calculateCRC32WithFileChannel(target);
                        } else if (cryptoMODE == CryptoMODE.ENCRYPTION) {
                            //Because only Target is Encrypted. Source is Clear Text or Encrypted by other software
                            //Decryption is only for restoring the encrypted file in clear text (decrypted).
                            //We always check cypher CRC32 value pairs.
                            crc32Source = calculateEncryptedCRC32WithFileChannel(source);
                            crc32Target = calculateCRC32WithFileChannel(target);
                        } else if (cryptoMODE == CryptoMODE.DECRYPTION) {
                            crc32Source = calculateCRC32WithFileChannel(source);
                            crc32Target = calculateEncryptedCRC32WithFileChannel(target);
                        }
                        else {
                            crc32Source = calculateCRC32WithFileChannel(source);
                            crc32Target = calculateCRC32WithFileChannel(target);
                        }

                    System.out.println("CRC32Source: " + crc32Source.getValue() + " SourcePath: " + source +  "CryptoMode: " + cryptoMODE);
                        System.out.println("CRC32Target: " + crc32Target.getValue() + " TargetPath: " + "CryptoMode: " + target);
                        if (crc32Source.getValue() == crc32Target.getValue()) {
                                validFilesCount.addAndGet(1);
                        }
                        else{
                            String log = (String.format("%-20s%s%n", "LogTime:", LocalDateTime.now().toString()));
                            log += String.format("%-20s%s%n","Source:", source.toString());
                            log += String.format("%-20s%s%n","Source Value:", crc32Source.getValue());
                            log += String.format("%-20s%s%n","Target:", target.toString());
                            log += String.format("%-20s%s%n","Target Value:", crc32Target.getValue());
                            LogFileWriterService.writeValidationLogFile(model.getUid(), LocalDateTime.now(), LogLEVEL.WARN, ValidationTYPE.CRC32,log);
                        }

                    Platform.runLater(() -> {
                        model.setValidFilesCount(validFilesCount.intValue());
                        model.setTotalVisitedFiles(fileTreeVisited.get());
                        validate(false);
                    });
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
        return validFilesCount;
    }

    public synchronized boolean validate(boolean isCalculationFinished){
        if(isCalculationFinished && this.model.getValidFilesCount() == this.model.getTotalVisitedFiles()){
            return true;
        }
        else {
            return false;
        }
    }

    public boolean calculateAndSaveCRC32Validation(CryptoMODE cryptoMODE) {
        System.out.println("Starting Validation Service");

        super.finishCalculations(this.model);
        int validFilesCount = checkAllFilesCRC32(this.model.getSource(), this.model.getTarget(), cryptoMODE);
        super.finishCalculations(this.model);
        Platform.runLater(() -> {
            this.model.setValidFilesCount(validFilesCount);
            this.model.setTotalVisitedFiles(fileTreeVisited.get());
            this.validate(true);
        });

        this.model.setValidationJob(false);
        super.finishCalculations(this.model);

        App.DataStore.saveModelAsJSON(this.model);

        System.out.println("SOURCE HASH IS " + this.model.getValidFilesCount());
        System.out.println("TARGET HASH IS " + this.model.getTotalVisitedFiles());

        return this.model.getValidFilesCount() != 0 && (this.model.getTotalVisitedFiles() == 0 ? false : true);

    }

}

