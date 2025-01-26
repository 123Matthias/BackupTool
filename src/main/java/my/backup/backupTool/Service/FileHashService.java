package my.backup.backupTool.Service;

import my.backup.backupTool.MessageTYPE;
import my.backup.backupTool.Model.IModel;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.CRC32;
import java.util.zip.CRC32C;


public class FileHashService {

    private IMessageList messageList;


    public FileHashService() {
        messageList = new MessageList();
    }

    public synchronized static byte[] calculateHash(Path file, String algorithm) {
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

    public static synchronized byte[] concatHash(byte[] oldHash, byte[] newHash) {
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


    public static synchronized CRC32 concatCRC32(CRC32 oldCRC32, CRC32 newCRC32) {
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
    private static byte[] longToByteArray(long value) {
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


    public static CRC32 calculateCRC32(File file) {
        // Initialisiere den CRC32-Algorithmus
        CRC32 crc32 = new CRC32();

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];  // Puffer für Datei
            int bytesRead;

            // Lese die Datei Stück für Stück und berechne CRC32
            while ((bytesRead = fis.read(buffer)) != -1) {
                crc32.update(buffer, 0, bytesRead);  // Update des CRC32 mit den gelesenen Bytes
            }
        } catch (IOException e) {
            e.printStackTrace();  // Fehlerbehandlung
        }

        return crc32;  // Gib den CRC32-Wert als long zurück
    }



    private static byte[] longToBytes(long value) {
        byte[] bytes = new byte[8];
        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) (value >>> (i * 8));
        }
        return bytes;
    }


    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


    public static long calculateHashDirectory(String path) {

        Path sourceDir = Paths.get(path);

        final AtomicReference<CRC32> oldCRC32 = new AtomicReference<>(new CRC32());

        try {
            // Durchlaufe das Verzeichnis
            Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
                // Besuche jede Datei im Verzeichnis
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    // Berechne den Hash der Datei
                    CRC32 newCRC32 = calculateCRC32(file.toFile());
                    System.out.println("next CRC32: " + newCRC32);

                    oldCRC32.set(concatCRC32(oldCRC32.get(), newCRC32));


                    // Aktualisiere den alten Hash, indem du die Referenz änderst


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

        // Rückgabe des finalen Hashes nach dem Verzeichnisdurchlauf
        return oldCRC32.get().getValue();
    }

}

