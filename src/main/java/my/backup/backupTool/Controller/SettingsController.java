package my.backup.backupTool.Controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import my.backup.backupTool.App;
import my.backup.backupTool.Enumerations.MessageTYPE;
import my.backup.backupTool.Enumerations.Theme;
import my.backup.backupTool.Notifications.MessageService;

import java.io.File;


public class SettingsController {

    @FXML
    private VBox hardware;

    @FXML
    private TextField storagePath;

    @FXML
    private CheckBox checkBoxSaveOnClose;

    @FXML
    private TextField executorPool;


    @FXML
    private void initialize() {
        this.createHWInfoRows();
        this.storagePath.setText(App.Properties.getSuperPath());
        this.checkBoxSaveOnClose.setSelected(App.Properties.isSaveOnCloseWindow());
        System.out.println(App.Properties.toString());

    }

    @FXML
    public void openDirectoryChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();

        directoryChooser.setTitle("Select Folder");
        File selectedFile = directoryChooser.showDialog(storagePath.getScene().getWindow());

        if (selectedFile != null) {
            storagePath.setText(selectedFile.getAbsolutePath());
        }

    }

    @FXML
    public void handleLightTheme(){
        App.Properties.setTheme(Theme.LIGHT);
        App.Router.setTheme(App.Properties.getTheme());
        App.Router.getMainStage().setScene(App.Router.getMainScene());
        App.Router.getSettigsStage().setScene(App.Router.getSettingsScene());
        App.Router.getMainStage().show();
    }

    @FXML
    public void handleDarkTheme(){
        App.Properties.setTheme(Theme.DARK);
        App.Router.setTheme(App.Properties.getTheme());
        App.Router.getMainStage().setScene(App.Router.getMainScene());
        App.Router.getSettigsStage().setScene(App.Router.getSettingsScene());
        App.Router.getMainStage().show();
    }


    @FXML
    public void createHWInfoRows() {
        App.Hardware = App.Hardware.getHardwareInfo();
        if(App.Properties.getThreadCount() <= 0){
            App.Properties.setThreadCount(App.Hardware.preferredThreadCount());
        }
        Label title = new Label("JVM Runtime Info:");
        title.getStyleClass().add("header");
        HBox hBox = createTextRow("available Processors: ", String.valueOf(App.Hardware.availableProcessors()));
        HBox hBox1 = createEditableTextRow("copy executor pool: ", String.valueOf(App.Properties.getThreadCount()),"executorPool");
        TextField field = (TextField) hBox1.lookup("#executorPool");
        field.getStyleClass().add("inputField");
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.isEmpty() || newValue.isBlank()){
                return;
            }
            else if (newValue.matches("\\d+")) {
                if (Integer.parseInt(newValue) < 1) {
                    MessageService.createToast("Wrong Thread Value", MessageTYPE.VALIDATION);
                    field.setText(oldValue);
                }
            }
            else {
                field.setText(oldValue);
                MessageService.createToast("Only numbers are allowed", MessageTYPE.VALIDATION);
            }
        });
        HBox hBox2 = createTextRow("total memory:", String.valueOf(App.Hardware.totalMemory()/1000/1000) + " MB");
        HBox hBox3 = createTextRow("free memory:", String.valueOf(App.Hardware.freeMemory()/1000/1000) + " MB");
        HBox hBox4 = createTextRow("screen width:", String.valueOf(App.Hardware.screenWidth()));
        HBox hBox5 = createTextRow("screen height:", String.valueOf(App.Hardware.screenHeight()));

        Label buffer = new Label("Copy Service Memory Buffer:");
        buffer.getStyleClass().add("header");
        HBox hBox6 = createBufferTextRow("if", "fileSize < 5 MiB", "THEN", "64 KiB");
        HBox hBox7 = createBufferTextRow("else if", "fileSize < 10 MiB", "THEN", "128 KiB");
        HBox hBox8 = createBufferTextRow("else if", "fileSize < 50 MiB", "THEN", "256 KiB");
        HBox hBox9 = createBufferTextRow("else if", "fileSize < 1 GiB", "THEN", "1 MiB");
        HBox hBox10 = createBufferTextRow("else", "------------------", "----", "2 MiB");
        this.hardware.getChildren().clear();
        this.hardware.getChildren().addAll(title, hBox, hBox2, hBox3, hBox4, hBox5, hBox1, buffer, hBox6, hBox7, hBox8, hBox9, hBox10);
    }
    private HBox createTextRow(String title, String value) {
        HBox hBox = new HBox();
        hBox.getStyleClass().add("keyValueRow");
        Label key = new Label(title);
        key.getStyleClass().add("description");
        Label valueLabel = new Label(value);

        hBox.getChildren().addAll(key, valueLabel);
        return hBox;
    }
    private HBox createEditableTextRow(String title, String value, String id) {
        HBox hBox = new HBox();
        hBox.getStyleClass().add("keyValueRow");

        Label key = new Label(title);
        key.getStyleClass().add("description");

        TextField inputField = new TextField(value);
        inputField.setId(id);

        inputField.setAlignment(Pos.CENTER_LEFT);
        inputField.getStyleClass().add("inputField");
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().addAll(key, inputField);

        return hBox;
    }
    private HBox createBufferTextRow(String value, String value1, String value2, String value3) {
        HBox hBox = new HBox();
        hBox.getStyleClass().add("bufferRow");
        Label when = new Label(value);
        when.getStyleClass().add("description");
        Label valueLabel1 = new Label(value1);
        Label valueLabel2 = new Label(value2);
        Label valueLabel3 = new Label(value3);
        valueLabel1.getStyleClass().add("bufferLabel1");
        valueLabel2.getStyleClass().add("bufferLabel2");
        valueLabel3.getStyleClass().add("bufferLabel3");

        hBox.getChildren().addAll(when, valueLabel1, valueLabel2, valueLabel3);
        return hBox;
    }

    @FXML
    private void save() {

        setAllStoragePaths();
        Node node = this.hardware.lookup("#executorPool");
        if(node instanceof TextField field) {
            if(field.getText().isEmpty() || field.getText().isBlank()) {
                App.Properties.setThreadCount(App.Hardware.preferredThreadCount());
            }
            else{
                App.Properties.setThreadCount(Integer.parseInt(field.getText()));
            }
        }
        App.SettingsDataStore.saveAppSettings();

    }

    private void setAllStoragePaths(){
        if(App.Properties.validatePath(storagePath.getText())){
            App.Properties.setSuperPath(storagePath.getText());
            App.SettingsDataStore.createLogFilePathIfNotExists(storagePath.getText());
            App.SettingsDataStore.createMergeModelPathIfNotExists(storagePath.getText());
            App.SettingsDataStore.createValidationLogFilePathIfNotExists(storagePath.getText());
        }
    }

    @FXML
    private void saveOnCloseWindow() {
        App.Properties.setSaveOnCloseWindow(this.checkBoxSaveOnClose.isSelected());
        System.out.println("Is save on close window enabled = " + App.Properties.isSaveOnCloseWindow());
    }
}
