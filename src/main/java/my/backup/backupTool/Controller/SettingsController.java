package my.backup.backupTool.Controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import my.backup.backupTool.App;
import my.backup.backupTool.Enumerations.MessageTYPE;
import my.backup.backupTool.Enumerations.Theme;
import my.backup.backupTool.Notifications.MessageService;


public class SettingsController {

    @FXML
    private VBox hardware;


    @FXML
    private void initialize() {

        this.createHWInfoRows();
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
        title.getStyleClass().add("headerText");
        HBox hBox = createTextRow("available Processors: ", String.valueOf(App.Hardware.availableProcessors()));
        HBox hBox1 = createEditableTextRow("copy executor pool: ", String.valueOf(App.Properties.getThreadCount()),"executorPool");

        TextField field = (TextField) hBox1.lookup("#executorPool");
            field.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    App.Properties.setThreadCount(Integer.parseInt(newValue));
                    MessageService.createToast("Thread pool saved",MessageTYPE.SAVE);
                } catch (NumberFormatException e) {
                    if(newValue != null && !newValue.isEmpty()){
                        MessageService.createToast("Invalid Value", MessageTYPE.VALIDATION);
                    }

                }
            });


        HBox hBox2 = createTextRow("total memory: ", String.valueOf(App.Hardware.totalMemory()/1000/1000) + " MB");
        HBox hBox3 = createTextRow("free memory: ", String.valueOf(App.Hardware.freeMemory()/1000/1000) + " MB");
        HBox hBox4 = createTextRow("screen width: ", String.valueOf(App.Hardware.screenWidth()));
        HBox hBox5 = createTextRow("screen height: ", String.valueOf(App.Hardware.screenHeight()));
        this.hardware.getChildren().clear();
        this.hardware.getChildren().addAll(title, hBox, hBox1, hBox2, hBox3, hBox4, hBox5);
    }
    private HBox createTextRow(String title, String value) {
        HBox hbox = new HBox(10);
        Label label = new Label(title);
        Label valueLabel = new Label(value);

        label.setMinWidth(170);

        hbox.getChildren().addAll(label, valueLabel);
        return hbox;
    }
    private HBox createEditableTextRow(String title, String value, String id) {
        HBox hbox = new HBox(10);
        Label label = new Label(title);
        label.setMinWidth(170);
        label.setStyle("-fx-font-style: italic; -fx-font-size: 10pt;");

        TextField textField = new TextField(value);
        textField.setId(id);
        textField.setMinWidth(20);
        textField.setMaxWidth(30);
        textField.setMaxHeight(20);
        textField.setPadding(new Insets(3));
        textField.setStyle("-fx-font-size: 10pt;");

        textField.setAlignment(Pos.CENTER_LEFT);

        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.getChildren().addAll(label, textField);

        return hbox;
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
