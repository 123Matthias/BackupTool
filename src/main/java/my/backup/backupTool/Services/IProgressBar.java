package my.backup.backupTool.Services;

public interface IProgressBar {
    void onProgressUpdate(double progress); // z. B. Wert zwischen 0 und 1
    void onStatusMessage(String message);   // Für Statusmeldungen
}
