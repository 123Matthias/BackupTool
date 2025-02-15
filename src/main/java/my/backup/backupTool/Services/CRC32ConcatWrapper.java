package my.backup.backupTool.Services;

import java.util.zip.CRC32;

public class CRC32ConcatWrapper {
    private CRC32 combinedCRC32;

    public CRC32ConcatWrapper() {
        this.combinedCRC32 = new CRC32();
    }

    public void concat(CRC32 crc32) {
        // Kombiniert den aktuellen CRC32-Wert mit dem vorherigen
        combinedCRC32.update((int) crc32.getValue());
    }

    public CRC32 getCombinedCRC32() {
        return combinedCRC32;
    }

    public void reset() {
        // Zurücksetzen für den nächsten Batch
        combinedCRC32.reset();
    }
}
