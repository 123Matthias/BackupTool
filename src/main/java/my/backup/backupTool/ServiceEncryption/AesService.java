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
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AesService extends BaseCalculationService implements ICopyService {

    private final BaseModel model;
    private final Cipher cipher;

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
        this.cipher = initCipher(model.TransientProperties.getSecretKey(), model.TransientProperties.getInitVector());
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

    public synchronized void copyFileWithFileChannel(Path inputPath, Path outputPath, CryptoMode cryptoMode) {
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


            // Nach der Verarbeitung aller Blöcke den finalen Block verarbeiten (wichtig für Padding)
            byte[] finalBlock = cipher.doFinal();
            if (finalBlock.length > 0) {
                outputChannel.write(ByteBuffer.wrap(finalBlock));  // Letzte verschlüsselte Daten schreiben
            }


            super.updateProgressBar(this.model);
            super.calculateWorkingSpeed(this.model);


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }


    public synchronized void decryptFileWithFileChannel(Path inputPath, Path outputPath) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, model.TransientProperties.getSecretKey(), model.TransientProperties.getInitVector());
        } catch (InvalidKeyException ex) {
            throw new RuntimeException(ex);
        } catch (InvalidAlgorithmParameterException ex) {
            throw new RuntimeException(ex);
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
                byte[] inputData = new byte[buffer.remaining()];
                buffer.get(inputData);
                byte[] decryptedData = cipher.update(inputData);
                outputChannel.write(ByteBuffer.wrap(decryptedData));
                System.out.println("Entschlüsselung: " + new String(decryptedData, StandardCharsets.UTF_8));

                super.addFileProcessedSize(buffer.remaining());
                buffer.clear();

                if (inputChannel.size() >= 50 * 1024 * 1024) {
                    super.updateProgressBar(this.model);
                    super.calculateWorkingSpeed(this.model);
                }
            }

            // Nach der Verarbeitung aller Blöcke den finalen Block entschlüsseln (wichtig für Padding)
            byte[] finalBlock = null;
            try {
                finalBlock = cipher.doFinal();
            } catch (IllegalBlockSizeException e) {
                throw new RuntimeException(e);
            } catch (BadPaddingException e) {
                throw new RuntimeException(e);
            }
            if (finalBlock.length > 0) {
                outputChannel.write(ByteBuffer.wrap(finalBlock));  // Letzte entschlüsselte Daten schreiben
            }

            super.updateProgressBar(this.model);
            super.calculateWorkingSpeed(this.model);


        } catch (IOException e) {
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


    // Setzen der echten Typen (SecretKey, IvParameterSpec) und deren Base64-Darstellung
    public void generateNewSecretKeyAndIVectorAndSetThem() {
        // Generiere SecretKey und IV
        SecretKey secretKey = generateAESKey();
        IvParameterSpec ivParameterSpec = generateIV();

        // Base64-kodierte Versionen der Schlüssel und IV erzeugen
        String secretKeyBase64 = encodeKey(secretKey);
        String ivBase64 = Base64.getEncoder().encodeToString(ivParameterSpec.getIV());

        // Setzen der echten Typen (SecretKey, IvParameterSpec) in TransientProperties
        model.TransientProperties.setSecretKey(secretKey);
        model.TransientProperties.setInitVector(ivParameterSpec);

        // Setzen der Base64-kodierten Strings im Modell
        model.setSecretKey(secretKeyBase64);
        model.setInitVector(ivBase64);
    }


    // Auslesen der Base64-kodierten Strings und Umwandeln in echte Typen
    public void getExistingSecretKeyAndIVectorFromModel() {
        // Holen der Base64-kodierten Strings aus dem Model
        String secretKeyBase64 = model.getSecretKey();
        String ivBase64 = model.getInitVector();

        // Umwandeln der Base64-Strings in die echten Typen
        byte[] decodedSecretKey = Base64.getDecoder().decode(secretKeyBase64);
        model.TransientProperties.setSecretKey(new SecretKeySpec(decodedSecretKey, "AES"));

        byte[] decodedIv = Base64.getDecoder().decode(ivBase64);
        model.TransientProperties.setInitVector(new IvParameterSpec(decodedIv));

        // Jetzt hast du den SecretKey und den IvParameterSpec im richtigen Typ
        // Du kannst damit weiterarbeiten, z.B. in einem Verschlüsselungsalgorithmus
    }
}
