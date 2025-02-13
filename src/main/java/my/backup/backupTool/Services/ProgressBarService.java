package my.backup.backupTool.Services;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class ProgressBarService implements IProgressBar {

    private ProgressBar progressBar;
    private Label statusLabel;

    public ProgressBarService(ProgressBar progressBar, Label statusLabel) {
        this.progressBar = progressBar;
        this.statusLabel = statusLabel;
    }

    @Override
    public void onProgressUpdate(double progress) {
        Platform.runLater(() -> progressBar.setProgress(progress));
    }

    @Override
    public void onStatusMessage(String message) {
        Platform.runLater(() -> statusLabel.setText(message));
    }

}
