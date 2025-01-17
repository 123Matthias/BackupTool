module my.backup.backupTool {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    opens my.backup.backupTool to javafx.fxml;
    opens my.backup.backupTool.Controller to javafx.fxml;
    exports my.backup.backupTool;
    exports my.backup.backupTool.Controller;
    exports my.backup.backupTool.Model to com.fasterxml.jackson.databind;
}

