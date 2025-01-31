package my.backup.backupTool.Encryption;

import javax.crypto.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.spec.IvParameterSpec;
import java.io.OutputStream;
import java.util.Base64;

public class AesService extends CipherOutputStream {

    public AesService(OutputStream out, SecretKey secretKey, IvParameterSpec iv) {
        super(out, initCipher(secretKey, iv));
    }

    private static Cipher initCipher(SecretKey secretKey, IvParameterSpec iv) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        } catch (NoSuchPaddingException e) {

        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
        return cipher;
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
