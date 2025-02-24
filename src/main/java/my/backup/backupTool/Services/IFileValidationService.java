package my.backup.backupTool.Services;

import my.backup.backupTool.Enumerations.CryptoMODE;

import java.nio.file.Path;

public interface IFileValidationService {


    byte[] calculateHash(Path file, String algorithm);

    byte[] concatHash(byte[] oldHash, byte[] newHash);



    String bytesToHex(byte[] bytes);

    boolean calculateAndSaveCRC32Validation( CryptoMODE cryptoMode);

}
