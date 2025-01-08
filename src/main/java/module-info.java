module my.backup.backuptool {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens my.backup.backuptool to javafx.fxml;
    opens my.backup.backuptool.Controller to javafx.fxml;
    exports my.backup.backuptool;
    exports my.backup.backuptool.Controller;
}

