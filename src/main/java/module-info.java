module my.backup.backupTool {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.annotation;

    opens my.backup.backupTool to javafx.fxml;
    opens my.backup.backupTool.Controller to javafx.fxml;
    exports my.backup.backupTool;
    exports my.backup.backupTool.Controller;
}

