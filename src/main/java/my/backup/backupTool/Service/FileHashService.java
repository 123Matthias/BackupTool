package my.backup.backupTool.Service;

import my.backup.backupTool.MessageTYPE;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class FileHashService {

    private IMessageList messageList;

    public FileHashService() {
        messageList = new MessageList();
    }

    public static String calculateHash(Path file, String algorithm) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = null;

            digest = MessageDigest.getInstance(algorithm);

            InputStream is = Files.newInputStream(file);

            byte[] buffer = new byte[8192]; // 8 KB Puffer
            int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    digest.update(buffer, 0, bytesRead);
                }


        byte[] hashBytes = digest.digest();
        return bytesToHex(hashBytes);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}
