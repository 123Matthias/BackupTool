package my.backup.backupTool.JobManagement;

import javafx.application.Platform;

public record Hardware(int availableProcessors, int preferredThreadCount, long totalMemory, long freeMemory) {

    public static Hardware getHardwareInfo() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int preferredThreadCount = (int)(availableProcessors * 0.8);
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();

        return new Hardware(availableProcessors, preferredThreadCount, totalMemory, freeMemory);
    }
}
