package my.backup.backupTool.Controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
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
    private Button soragePathButton;


    @FXML
    private void initialize() {
        this.createHWInfoRows();
        this.storagePath.setText(App.Properties.getSuperPath());
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
        App.Router.setTheme(Theme.LIGHT);
        App.Router.getMainStage().setScene(App.Router.getMainScene());
        App.Router.getSettigsStage().setScene(App.Router.getSettingsScene());
        App.Router.getMainStage().show();
    }

    @FXML
    public void handleDarkTheme(){
        App.Router.setTheme(Theme.DARK);
        App.Router.getMainStage().setScene(App.Router.getMainScene());
        App.Router.getSettigsStage().setScene(App.Router.getSettingsScene());
        App.Router.getMainStage().show();
    }

    @FXML
    public void backToMain(){
        //  Main.mainStage.setScene(Main.sceneMain);
        App.Router.getMainStage().setScene(App.Router.getMainScene());
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

        HBox hBox2 = createTextRow("total memory: ", String.valueOf(App.Hardware.totalMemory()/1000/1000) + " MB");
        HBox hBox3 = createTextRow("free memory: ", String.valueOf(App.Hardware.freeMemory()/1000/1000) + " MB");
        HBox hBox4 = createTextRow("screen width: ", String.valueOf(App.Hardware.screenWidth()));
        HBox hBox5 = createTextRow("screen height: ", String.valueOf(App.Hardware.screenHeight()));
        this.hardware.getChildren().clear();
        this.hardware.getChildren().addAll(title, hBox, hBox2, hBox3, hBox4, hBox5, hBox1);
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

    @FXML
    private void save() {
        setAllStoragePaths();
    }

    @FXML
    private void setAllStoragePaths(){
        if(App.Properties.validatePath(storagePath.getText())){
            App.Properties.setSuperPath(storagePath.getText());
            App.SettingsDataStore.createLogFilePathIfNotExists(storagePath.getText());
            App.SettingsDataStore.createMergeModelPathIfNotExists(storagePath.getText());
            App.SettingsDataStore.createValidationLogFilePathIfNotExists(storagePath.getText());
        }
    }

    /*
    @FXML
    public void setLightTheme() {

        App.Router.clearAllStylesheets();

        // Setze den Hintergrund für das helle Thema
      root.setStyle("-fx-background-color: linear-gradient(to bottom right, "
                + "rgba(230,230,235, 1) 20%, "
                + "rgba(240,240,245, 1) 60%, "
                + "rgba(250,250,255, 1) 80%, "
                + "rgba(255,255,255, 1) 100%);");
    }

    @FXML
    public void setDarkTheme() {
        // Setze den Hintergrund für das dunkle Thema
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, "
                + "rgba(30,30,35, 1) 20%, "
                + "rgba(40,40,45, 1) 60%, "
                + "rgba(50,50,55, 1) 80%, "
                + "rgba(60,60,65, 1) 100%);");
    }
*/
}
