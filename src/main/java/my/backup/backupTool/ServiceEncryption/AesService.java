package my.backup.backupTool.ServiceEncryption;

import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.Services.BaseCalculationService;
import my.backup.backupTool.Services.ICopyService;

import javax.crypto.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.concurrent.locks.ReentrantLock;

public class AesService extends BaseCalculationService implements ICopyService {

    private final BaseModel model;

    ReentrantLock lock = new ReentrantLock();

    public AesService(BaseModel model) {
        this.model = model;

        String secretKeyBase64 = model.getSecretKey();
        String ivBase64 = model.getInitVector();

        if (secretKeyBase64 == null || secretKeyBase64.isEmpty() || ivBase64 == null || ivBase64.isEmpty()) {
            this.generateNewSecretKeyAndIVectorAndSetThem();
        } else {
            this.getExistingSecretKeyAndIVectorFromModel();
        }
        System.out.println("Secret Key: " + secretKeyBase64);
        System.out.println("IV: " + ivBase64);

    }



    private static Cipher initCipherEncryption(SecretKey secretKey, GCMParameterSpec iv) {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException("Fehler bei der Initialisierung des Ciphers", e);
        }
    }

    private static Cipher initCipherDecryption(SecretKey secretKey, GCMParameterSpec iv) {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException("Fehler bei der Initialisierung des Ciphers", e);
        }
    }

    public void copyFileWithFileChannel(Path inputPath, Path outputPath, CryptoMode cryptoMode) {
      Cipher cipher = initCipherEncryption(model.TransientProperties.getSecretKey(), model.TransientProperties.getInitVector());
       if(cryptoMode == CryptoMode.DECRYPTION){
           decryptFileWithFileChannel(inputPath, outputPath);
           return;
       }

        try (FileChannel inputChannel = FileChannel.open(inputPath, StandardOpenOption.READ);
             FileChannel outputChannel = FileChannel.open(outputPath, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {

            ByteBuffer buffer;

            if(inputChannel.size() < 5*1024*1024) {
                buffer = ByteBuffer.allocate((int)super.DEFAULT_BUFFERSIZE);
            }
            else{
                buffer = ByteBuffer.allocate((int)super.calculateBufferSize(inputChannel.size()));
            }

            while (inputChannel.read(buffer) != -1) {
                buffer.flip();
                // Erstellen eines Arrays für die verschlüsselten Daten
                byte[] inputData = new byte[buffer.remaining()];
                buffer.get(inputData);
                byte[] encryptedData = cipher.update(inputData);
                outputChannel.write(ByteBuffer.wrap(encryptedData)); // Verschlüsselte Daten schreiben

                super.addFileProcessedSize(encryptedData.length); // Fortschritt berechnen
                buffer.clear();

                if (inputChannel.size() >= 50 * 1024 * 1024) {
                    super.updateProgressBar(this.model);
                    super.calculateWorkingSpeed(this.model);
                }
            }

            super.updateProgressBar(this.model);
            super.calculateWorkingSpeed(this.model);

            // Finalen Block entschlüsseln und schreiben
            byte[] finalBlock = cipher.doFinal();
            if (finalBlock != null && finalBlock.length > 0) {
                outputChannel.write(ByteBuffer.wrap(finalBlock));
            }


        } catch (IOException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }

    }



    public void decryptFileWithFileChannel(Path inputPath, Path outputPath) {

        Cipher cipher = initCipherDecryption(model.TransientProperties.getSecretKey(), model.TransientProperties.getInitVector());

        try (FileChannel inputChannel = FileChannel.open(inputPath, StandardOpenOption.READ);
             FileChannel outputChannel = FileChannel.open(outputPath, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {

            ByteBuffer buffer;

            if(inputChannel.size() < 5*1024*1024) {
                buffer = ByteBuffer.allocate((int)super.DEFAULT_BUFFERSIZE);
            }
            else{
                buffer = ByteBuffer.allocate((int)super.calculateBufferSize(inputChannel.size()));
            }

            while (inputChannel.read(buffer) != -1) {
                buffer.flip();
                byte[] inputData = new byte[buffer.remaining()];
                buffer.get(inputData);
                byte[] decryptedData = cipher.update(inputData);
                outputChannel.write(ByteBuffer.wrap(decryptedData));
                System.out.println("Entschlüsselung: " + new String(decryptedData, StandardCharsets.UTF_8));

                super.addFileProcessedSize(inputData.length);
                buffer.clear();

                if (inputChannel.size() >= 50 * 1024 * 1024) {
                    super.updateProgressBar(this.model);
                    super.calculateWorkingSpeed(this.model);
                }

            }

            super.updateProgressBar(this.model);
            super.calculateWorkingSpeed(this.model);

            // Finalen Block entschlüsseln und schreiben
            byte[] finalBlock = cipher.doFinal();
            if (finalBlock != null && finalBlock.length > 0) {
                outputChannel.write(ByteBuffer.wrap(finalBlock));
            }


        } catch (IOException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }


    public static SecretKey generateAESKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256); // Sichere 256-Bit-Schlüsselgröße
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("AES-Algorithmus nicht gefunden", e);
        }
    }


    public static IvParameterSpec generateIV() {
        byte[] iv = new byte[16]; // 16 Byte IV für AES
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static String encodeKey(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public BaseModel getModel() {
        return model;
    }


    public void generateNewSecretKeyAndIVectorAndSetThem() {
        // Generiere SecretKey und 12-Byte IV für GCM
        SecretKey secretKey = generateAESKey();
        byte[] genIV = generateGCMIV();
        GCMParameterSpec iv = new GCMParameterSpec(128,genIV);

        // Base64-kodierte Versionen erzeugen
        String secretKeyBase64 = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        String ivBase64 = Base64.getEncoder().encodeToString(genIV);

        // Setzen der echten Typen (SecretKey, IV als Byte-Array) in TransientProperties
        model.TransientProperties.setSecretKey(secretKey);
        model.TransientProperties.setInitVector(iv);

        // Setzen der Base64-kodierten Strings im Modell
        model.setSecretKey(secretKeyBase64);
        model.setInitVector(ivBase64);
    }


    public void getExistingSecretKeyAndIVectorFromModel() {
        // Holen der Base64-kodierten Strings aus dem Model
        String secretKeyBase64 = model.getSecretKey();
        String ivBase64 = model.getInitVector();

        // Umwandeln der Base64-Strings in echte Typen
        byte[] decodedSecretKey = Base64.getDecoder().decode(secretKeyBase64);
        SecretKey secretKey = new SecretKeySpec(decodedSecretKey, "AES");

        byte[] decodedIv = Base64.getDecoder().decode(ivBase64);
        GCMParameterSpec iv = new GCMParameterSpec(128, decodedIv);
        // Setzen der echten Werte im Modell
        model.TransientProperties.setSecretKey(secretKey);
        model.TransientProperties.setInitVector(iv); // Byte-Array statt IvParameterSpec
    }


    private static byte[] generateGCMIV() {
        byte[] iv = new byte[12]; // GCM empfiehlt 12-Byte IV
        new SecureRandom().nextBytes(iv);
        return iv;
    }


}
