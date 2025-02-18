module my.backup.backupTool {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires java.compiler;
    requires java.desktop;
    requires java.sql;

    opens my.backup.backupTool to javafx.fxml;
    opens my.backup.backupTool.Controller to javafx.fxml;
    exports my.backup.backupTool;
    exports my.backup.backupTool.JobManagement;
    exports my.backup.backupTool.Controller;
    exports my.backup.backupTool.Notifications;
    exports my.backup.backupTool.Model;
    exports my.backup.backupTool.DataRepository;
    exports my.backup.backupTool.ServiceEncryption;
    opens my.backup.backupTool.ServiceEncryption to javafx.fxml;
    exports my.backup.backupTool.Services;
    exports my.backup.backupTool.Controller.Merge;
    opens my.backup.backupTool.Controller.Merge to javafx.fxml;
}

