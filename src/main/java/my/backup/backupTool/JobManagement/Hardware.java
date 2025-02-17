package my.backup.backupTool.JobManagement;

import javafx.application.Platform;
import javafx.stage.Screen;

public record Hardware(int availableProcessors, int preferredThreadCount, long totalMemory, long freeMemory, double screenWidth, double screenHeight) {

    public static Hardware getHardwareInfo() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int preferredThreadCount = (int)(availableProcessors * 0.8);
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        double screenWidth = Screen.getPrimary().getBounds().getWidth();

        return new Hardware(availableProcessors, preferredThreadCount, totalMemory, freeMemory, screenWidth, screenHeight);
    }
}
