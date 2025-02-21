package my.backup.backupTool.Services;

import com.fasterxml.jackson.databind.ser.Serializers;
import my.backup.backupTool.App;
import my.backup.backupTool.DataRepository.IDataStore;
import my.backup.backupTool.Model.BackupType;
import my.backup.backupTool.Model.BaseModel;
import my.backup.backupTool.Model.ValidationTYPE;
import my.backup.backupTool.Properties;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;

public class LogFileWriterService {



    public static boolean createFilePathIfNotExists() {
        File logDirectory = new File(App.Properties.getValidationLogFilePath()); // Das ist das Verzeichnis!
        if (!logDirectory.exists()) {
            System.out.println("Log file does not exist: " + logDirectory.getAbsolutePath());
            return logDirectory.mkdirs(); // Erstellt alle fehlenden Verzeichnisse
        }
        return false;
    }


    public synchronized static boolean writeLogFile(LocalDateTime dateTime, LogLevel logLevel, Enum enumType, String logMessage) {
        try {
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(App.Properties.getLogFilePath(), true));
            fileWriter.newLine();
            fileWriter.write(String.format("%-20s%s%n", "LogDate:", dateTime.toString()));
            fileWriter.write(String.format("%-20s%s%n","Level:", logLevel.toString()));
            fileWriter.write(String.format("%-20s%s%n","Type:", enumType.toString()));
            fileWriter.write(logMessage);
            fileWriter.flush();
            fileWriter.close();
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized static boolean writeValidationLogFile( String UUID, LocalDateTime dateTime, LogLevel logLevel, Enum enumType, String logMessage) {
        try {
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(App.Properties.getValidationLogFilePath() + "UUID=" + UUID + ".log", true));
            fileWriter.newLine();
            fileWriter.write(String.format("%-20s%s%n", "LogDate:", dateTime.toString()));
            fileWriter.write(String.format("%-20s%s%n","Level:", logLevel.toString()));
            fileWriter.write(String.format("%-20s%s%n","Type:", enumType.toString()));
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
