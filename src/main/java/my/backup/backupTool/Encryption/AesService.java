package my.backup.backupTool.Encryption;

import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.CopyServices.BaseCalculationService;
import my.backup.backupTool.CopyServices.ICopyService;

import javax.crypto.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;

public class AesService extends BaseCalculationService implements ICopyService {

    private final BaseModel model;
    private final Cipher cipher;

    public AesService(BaseModel model) {
        this.model = model;
        if(model.getInitVector() == null || model.getSecretKey() == null) {
            model.setInitVector(AesService.generateIV());
            model.setSecretKey(AesService.generateAESKey());
        }

        this.cipher = initCipher(model.getSecretKey(), model.getInitVector());
    }

    private static Cipher initCipher(SecretKey secretKey, IvParameterSpec iv) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException("Fehler bei der Initialisierung des Ciphers", e);
        }
    }

    public void copyFileWithFileChannel(Path inputPath, Path outputPath, long totalSize) {
        try (FileChannel inputChannel = FileChannel.open(inputPath, StandardOpenOption.READ);
             FileChannel outputChannel = FileChannel.open(outputPath, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {

            // buffering blocks and encrypting these blocks
            long fileProcessedSize = 0;
            ByteBuffer buffer = ByteBuffer.allocate(64*1024);
            while (inputChannel.read(buffer) != -1) {
                buffer.flip();
                byte[] encryptedData = cipher.update(buffer.array(), 0, buffer.remaining());
                outputChannel.write(ByteBuffer.wrap(encryptedData));
                fileProcessedSize += buffer.remaining();
                buffer.clear();

                double progress = (double) fileProcessedSize / totalSize;
                super.updateProgress(progress, this.model);
                super.calculateWorkingSpeed(fileProcessedSize,this.model);
            }

            byte[] finalBlock = cipher.doFinal();
            outputChannel.write(ByteBuffer.wrap(finalBlock));
        } catch (IOException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public static SecretKey generateAESKey() {
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }
        keyGen.init(256); // Sichere 256 Bit Schlüsselgröße
        return keyGen.generateKey();
    }

    public static IvParameterSpec generateIV() {
        byte[] iv = new byte[16]; // 16 Byte IV für AES
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static String encodeKey(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
}
