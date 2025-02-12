package my.backup.backupTool.CopyServices;

import java.io.File;
import java.nio.file.Path;
import java.util.zip.CRC32;

public interface IFileValidationService {


    byte[] calculateHash(Path file, String algorithm);

    byte[] concatHash(byte[] oldHash, byte[] newHash);

    CRC32 concatCRC32(CRC32 oldCRC32, CRC32 newCRC32);

    CRC32 calculateCRC32WithStream(File file);

    String bytesToHex(byte[] bytes);

    long getCRC32FromDirectory(String path);
    boolean calculateAndSaveCRC32Validation();

}
