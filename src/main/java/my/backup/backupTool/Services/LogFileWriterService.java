package my.backup.backupTool.Services;

import my.backup.backupTool.App;
import my.backup.backupTool.Model.BackupType;
import my.backup.backupTool.Model.ValidationTYPE;
import my.backup.backupTool.Properties;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class LogFileWriterService {


    public LogFileWriterService() {

    }

    public static boolean writeLogFile(LocalDateTime dateTime, LogLevel logLevel, Enum enumType, String logMessage) {
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

    public static boolean writeValidationLogFile( String UUID, LocalDateTime dateTime, LogLevel logLevel, Enum enumType, String logMessage) {
        try {
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(App.Properties.getLogFilePath() + "_UUID=" + UUID, false));
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
}
