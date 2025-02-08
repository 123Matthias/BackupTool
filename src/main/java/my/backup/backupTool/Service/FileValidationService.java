package my.backup.backupTool.Service;

import javafx.application.Platform;
import my.backup.backupTool.App;
import my.backup.backupTool.Model.BaseModel;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;


public class FileValidationService extends BaseCalculationService implements IFileValidationService {

    private long totalFileSize;
    private double progress;

    private IMessageList messageList;
    private BaseModel model;

    public FileValidationService(BaseModel model) {
        messageList = new MessageList();
        this.model = model;
        System.out.println("Model Hash Service:" + this.model);
    }

    public byte[] calculateHash(Path file, String algorithm) {
        MessageDigest digest = null;

        try {
            digest = MessageDigest.getInstance(algorithm);
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        try (InputStream is = Files.newInputStream(file)) {

            byte[] buffer = new byte[8192]; // 8 KB Puffer
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        byte[] hashBytes = digest.digest();
        return hashBytes;
    }

    public byte[] concatHash(byte[] oldHash, byte[] newHash) {
        MessageDigest digest = null;

        try {
            digest = MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
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

        // has to be a byte[]
        crc32.update(longToByteArray(oldValue));
        crc32.update(longToByteArray(newValue));
        return crc32;  // Gib das kombinierte CRC32-Objekt zurück
    }

    class CRC32Concat {
        private CRC32 crc32 = new CRC32();

        public void update(CRC32 newCRC32) {
            this.crc32 = concatCRC32(this.crc32, newCRC32);
        }

        public CRC32 getCRC32() {
            return crc32;
        }
        public void reset(){
            this.crc32.reset();
        }
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

//slow dont use this Method
    public CRC32 calculateCRC32WithStream(File file) {
        CRC32 crc32 = new CRC32();

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {

            byte[] buffer = new byte[8192];  // Puffer für Datei 8k standard Block ....
            int bytesRead;
            long fileProcessedSize = 0;

            while ((bytesRead = bis.read(buffer)) != -1) {
                crc32.update(buffer, 0, bytesRead);
                fileProcessedSize += bytesRead;
                this.progress = (double) fileProcessedSize / this.totalFileSize;
                super.updateProgress(progress,this.model);
                super.calculateWorkingSpeed(fileProcessedSize,this.model);
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return crc32;
    }

    protected CRC32 calculateCRC32WithFileChannel(Path sourceFile) {
        CRC32 crc32 = new CRC32();

        try (FileChannel sourceChannel = FileChannel.open(sourceFile, StandardOpenOption.READ)) {
            long fileProcessedSize = 0;
            long chunkSize = 1024 * 64; // 8K Blöcke

            ByteBuffer buffer = ByteBuffer.allocateDirect((int) chunkSize);

            while (fileProcessedSize < this.totalFileSize) {
                buffer.clear();  // Puffer für neuen Lesevorgang zurücksetzen
                int bytesRead = sourceChannel.read(buffer);

                if (bytesRead == -1) break; // Dateiende erreicht

                buffer.flip(); // In den Lesemodus wechseln
                crc32.update(buffer); // Direkte Übergabe an CRC32 ohne Byte-Array

                fileProcessedSize += bytesRead;
                double progress = (double) fileProcessedSize / this.totalFileSize;

                super.updateProgress(progress, this.model);
                super.calculateWorkingSpeed(fileProcessedSize, this.model);
            }
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


    public long getCRC32FromDirectory(String path) {
        super.updateProgress(0.0,this.model);
        Path sourcePath = Paths.get(path);
        long valueCRC32 = 0;
        try {
            this.totalFileSize = super.calculateTotalSize(sourcePath);
            valueCRC32 = calculateCRC32FromPath(sourcePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return valueCRC32;

    }

    private long calculateCRC32FromPath(Path sourcePath) throws IOException {

        CRC32Concat crc32Root = new CRC32Concat();

            Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    CRC32 newCRC32 = calculateCRC32WithFileChannel(file);
                    //System.out.println("next CRC32: " + newCRC32.getValue());
                    crc32Root.update(newCRC32);
                    //System.out.println(crc32Root.getCRC32().getValue());
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
        return crc32Root.getCRC32().getValue();
    }

    public boolean validate(){
        if(this.model.getSourceValidationValue().equals(this.model.getTargetValidationValue())){
            return true;
        }
        else {
            return false;
        }
    }


    public boolean calculateAndSaveCRC32Validation() {
        System.out.println("Making CRC32");
        super.finishCalculations(this.model);
        long crc32Source = getCRC32FromDirectory(this.model.getSource());
        super.finishCalculations(this.model);
        Platform.runLater(()->this.model.setSourceValidationValue(String.valueOf(crc32Source)));

        long crc32Target = getCRC32FromDirectory(this.model.getTarget());
        Platform.runLater(()->this.model.setTargetValidationValue(String.valueOf(crc32Target)));
        this.model.setValidationJob(false);
        super.finishCalculations(this.model);

        boolean validate = validate();

        App.DataStore.saveModelAsJSON(this.model);

        System.out.println("SOURCE HASH IS " + this.model.getSourceValidationValue());
        System.out.println("TARGET HASH IS " + this.model.getTargetValidationValue());

        return !this.model.getSourceValidationValue().isEmpty() && !this.model.getTargetValidationValue().isEmpty();

    }
}

