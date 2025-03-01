package my.backup.backupTool.Services;

import my.backup.backupTool.App;
import my.backup.backupTool.Enumerations.LogLEVEL;
import my.backup.backupTool.Enumerations.BackupTYPE;
import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.Enumerations.ValidationTYPE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class LogFileWriterService {

    public static boolean createFilePathIfNotExists() {
        String validationLogFilePath = App.Properties.getValidationLogFilePath();
        if (validationLogFilePath == null || !new File(validationLogFilePath).exists()) {
            System.out.println("Log file does not exist: " + validationLogFilePath);
            File logDirectory = new File(validationLogFilePath); // Das ist das Verzeichnis!
            return logDirectory.mkdirs(); // Erstellt alle fehlenden Verzeichnisse
        }
        return false;
    }



    public synchronized static boolean writeLogFile(LocalDateTime dateTime, LogLEVEL logLevel, BackupTYPE backupType, String logMessage) {
        try {
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(App.Properties.getLogFilePath(), true));
            fileWriter.newLine();
            fileWriter.write(String.format("%-20s%s%n","LogDate:", dateTime.toString()));
            fileWriter.write(String.format("%-20s%s%n","Level:", logLevel.toString()));
            fileWriter.write(String.format("%-20s%s%n","Type:", backupType.toString()));
            fileWriter.write(logMessage);
            fileWriter.flush();
            fileWriter.close();
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized static boolean writeValidationLogFile(String UUID, LocalDateTime dateTime, LogLEVEL logLevel, ValidationTYPE validationType, String logMessage) {
        try {
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(App.Properties.getValidationLogFilePath() + "UUID=" + UUID + ".log", true));
            fileWriter.newLine();
            fileWriter.write(String.format("%-20s%s%n", "LogDate:", dateTime.toString()));
            fileWriter.write(String.format("%-20s%s%n","Level:", logLevel.toString()));
            fileWriter.write(String.format("%-20s%s%n","Type:", validationType.toString()));
            fileWriter.write(logMessage);
            fileWriter.flush();
            fileWriter.close();
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean deleteOldValidationLogFile(BaseModel model) {
        File file = new File(App.Properties.getValidationLogFilePath() + "UUID=" + model.getUid() + ".log");
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }
}