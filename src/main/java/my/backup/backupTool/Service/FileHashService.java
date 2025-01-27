package my.backup.backupTool.Service;

import my.backup.backupTool.DataRepository.BaseDataRepository;
import my.backup.backupTool.DataRepository.IStoreData;
import my.backup.backupTool.Model.IModel;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.CRC32;


public class FileHashService extends BaseCalculationService implements IFileHashService {

    private long totalFileSize;
    private double progress;

    private IMessageList messageList;
    private IStoreData dataStore;
    private IModel model;

    public FileHashService(IModel model) {
        messageList = new MessageList();
        dataStore = new BaseDataRepository();
        this.model = model;
        System.out.println("Model Hash Service:" + this.model);
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
            // Initialisiere den MessageDigest für SHA-256 oder das gewünschte Algorithmus
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        // Füge den alten Hash und den neuen Hash zum Digest hinzu
        digest.update(oldHash);
        digest.update(newHash);

        // Gib den kombinierten Hash zurück
        return digest.digest();
    }


    public CRC32 concatCRC32(CRC32 oldCRC32, CRC32 newCRC32) {
        CRC32 crc32 = new CRC32();

        // Hole den CRC32-Wert aus den beiden CRC32-Objekten
        long oldValue = oldCRC32.getValue();
        long newValue = newCRC32.getValue();

        // Update CRC32 mit den Werten der beiden CRC32-Objekte
        crc32.update(longToByteArray(oldValue));  // Konvertiere den alten Wert in Bytes
        crc32.update(longToByteArray(newValue));  // Konvertiere den neuen Wert in Bytes
        return crc32;  // Gib das kombinierte CRC32-Objekt zurück
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


    public CRC32 calculateCRC32(File file) {
        CRC32 crc32 = new CRC32();

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {

            byte[] buffer = new byte[1024];  // Puffer für Datei
            int bytesRead;
            long fileProcessedSize = 0;

            // Lese die Datei Stück für Stück und berechne CRC32
            while ((bytesRead = bis.read(buffer)) != -1) {
                crc32.update(buffer, 0, bytesRead);  // Update des CRC32 mit den gelesenen Bytes
                fileProcessedSize += bytesRead;
                this.progress = (double) fileProcessedSize / this.totalFileSize;
                super.updateProgress(progress,this.model);

            }
        } catch (IOException e) {
            e.printStackTrace();  // Fehlerbehandlung
        }

        return crc32;  // Gib den CRC32-Wert als long zurück
    }



    public String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


    public long calculateHashFromDirectory(String path) {
        super.updateProgress(0.0,this.model);
        super.setLastProgressState(0.0);
        Path sourcePath = Paths.get(path);
        AtomicReference<CRC32> oldCRC32 = new AtomicReference<>(new CRC32());
        try {
            this.totalFileSize = super.calculateTotalSize(sourcePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {

                // Besuche jede Datei im Verzeichnis
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    // Berechne den Hash der Datei
                    CRC32 newCRC32 = calculateCRC32(file.toFile());
                    System.out.println("next CRC32: " + newCRC32.getValue());

                    oldCRC32.set(concatCRC32(oldCRC32.get(), newCRC32));
                    System.out.println(oldCRC32.get().getValue());

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
        } catch (IOException e) {
            System.err.println("Fehler beim Durchlaufen des Verzeichnisses: " + e.getMessage());
        }

        return oldCRC32.get().getValue();
    }



    public boolean calculateAndSaveHashes() {
        System.out.println("Making hashes");
        long hash = calculateHashFromDirectory(this.model.getSource());
        this.model.setSourceHash(String.valueOf(hash));
        long hashBackup = calculateHashFromDirectory(this.model.getTarget());
        this.model.setTargetHash(String.valueOf(hashBackup));
        this.model.setPlayBackupOrder(false);
        dataStore.saveModelAsJSON(this.model);
        this.model.setHashOrder(false);
        super.updateProgress(0.0,this.model);

        System.out.println("SOURCE HASH IS " + this.model.getSourceHash());
        System.out.println("TARGET HASH IS " + this.model.getTargetHash());

        return !this.model.getSourceHash().isEmpty() && !this.model.getTargetHash().isEmpty();

    }
}

