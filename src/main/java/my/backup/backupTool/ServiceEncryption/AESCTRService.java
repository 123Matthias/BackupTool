package my.backup.backupTool.ServiceEncryption;

import my.backup.backupTool.Enumerations.CryptoMODE;
import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.Services.BaseCalculationService;
import my.backup.backupTool.Services.ICopyService;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class AESCTRService extends BaseCalculationService implements ICopyService {

    private final BaseModel model;


    public AESCTRService(BaseModel model) {
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



        public static Cipher initCipherEncryption(SecretKey secretKey, IvParameterSpec iv) {
            try {
                Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
                return cipher;
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                     InvalidAlgorithmParameterException e) {
                throw new RuntimeException("Fehler bei der Initialisierung des Ciphers", e);
            }
        }

        public static Cipher initCipherDecryption(SecretKey secretKey, IvParameterSpec iv) {
            try {
                Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
                cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
                return cipher;
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
                throw new RuntimeException("Fehler bei der Initialisierung des Ciphers", e);
            }
        }


    public void copyFileWithFileChannel(Path inputPath, Path outputPath, CryptoMODE cryptoMode) {
        Cipher cipher = initCipherEncryption(model.TransientProperties.getSecretKey(), model.TransientProperties.getCTRInitVector());
        if(cryptoMode == CryptoMODE.DECRYPTION){
            decryptFileWithFileChannel(inputPath, outputPath);
            return;
        }

        try (FileChannel inputChannel = FileChannel.open(inputPath, StandardOpenOption.READ);
             FileChannel outputChannel = FileChannel.open(outputPath, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {

            ByteBuffer buffer;

            buffer = ByteBuffer.allocate((int)super.calculateBufferSize(inputChannel.size()));

            while (inputChannel.read(buffer) != -1) {
                buffer.flip();
                byte[] encryptedData = cipher.update(buffer.array(),buffer.position(),buffer.remaining());
                int copySize = outputChannel.write(ByteBuffer.wrap(encryptedData)); // Verschlüsselte Daten schreiben
                super.addFileProcessedSize(copySize); // Fortschritt berechnen

                if (inputChannel.size() >= 50 * 1024 * 1024) {
                    super.updateProgressBar(this.model);
                    super.calculateWorkingSpeed(this.model);
                }
            }

            // Finalen Block entschlüsseln und schreiben
            byte[] finalBlock = cipher.doFinal();
            if (finalBlock != null && finalBlock.length > 0) {
                int copySize = outputChannel.write(ByteBuffer.wrap(finalBlock));
                super.addFileProcessedSize(copySize); // Fortschritt berechnen
            }

            super.updateProgressBar(this.model);
            super.calculateWorkingSpeed(this.model);



        } catch (IOException | IllegalBlockSizeException | BadPaddingException e) {
            System.out.println("Error: " + e.getStackTrace());
        }

    }


    public void decryptFileWithFileChannel(Path inputPath, Path outputPath) {
        System.out.println("Encrypting Data CTR---->");
        Cipher cipher = initCipherDecryption(model.TransientProperties.getSecretKey(), model.TransientProperties.getCTRInitVector());

        try (FileChannel inputChannel = FileChannel.open(inputPath, StandardOpenOption.READ);
             FileChannel outputChannel = FileChannel.open(outputPath, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {

            ByteBuffer buffer;

            buffer = ByteBuffer.allocate((int)super.calculateBufferSize(inputChannel.size()));

            while (inputChannel.read(buffer) != -1) {
                buffer.flip();
                byte[] decryptedData = cipher.update(buffer.array(),buffer.position(),buffer.remaining());
                int copySize = outputChannel.write(ByteBuffer.wrap(decryptedData));
                super.addFileProcessedSize(copySize);
                buffer.clear();

                if (inputChannel.size() >= 50 * 1024 * 1024) {
                    super.updateProgressBar(this.model);
                    super.calculateWorkingSpeed(this.model);
                }
            }

            // Finalen Block entschlüsseln und schreiben
            byte[] finalBlock = cipher.doFinal();
            if (finalBlock != null && finalBlock.length > 0) {
                int copySize = outputChannel.write(ByteBuffer.wrap(finalBlock));
                super.addFileProcessedSize(copySize); // Fortschritt berechnen
            }

            super.updateProgressBar(this.model);
            super.calculateWorkingSpeed(this.model);


        } catch (IOException | IllegalBlockSizeException | BadPaddingException e) {
            System.out.println("Error: " + e.getStackTrace());
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
        byte[] genIV = generateCTRIV();
        IvParameterSpec iv = new IvParameterSpec(genIV); // Korrekte Nutzung für CTR

        // Base64-kodierte Versionen erzeugen
        String secretKeyBase64 = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        String ivBase64 = Base64.getEncoder().encodeToString(genIV);

        // Setzen der echten Typen (SecretKey, IV als Byte-Array) in TransientProperties
        model.TransientProperties.setSecretKey(secretKey);
        model.TransientProperties.setCTRInitVector(iv);

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
        model.TransientProperties.setGCMInitVector(iv); // Byte-Array statt IvParameterSpec
    }

    private static byte[] generateCTRIV() {
        byte[] iv = new byte[16]; // CTR benötigt 16-Byte-IV
        new SecureRandom().nextBytes(iv);
        return iv;
    }


}
