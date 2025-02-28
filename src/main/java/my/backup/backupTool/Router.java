package my.backup.backupTool;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import my.backup.backupTool.Controller.ExceptionController;
import my.backup.backupTool.Controller.MainController;
import my.backup.backupTool.Controller.SettingsController;
import my.backup.backupTool.Enumerations.Theme;

import java.io.IOException;

public class Router {

    private static Router Instance;
    private Stage mainStage;
    private Scene mainScene;
    private MainController mainController;
    private Scene settingsScene;
    private SettingsController settingsController;
    private Theme theme;
    private Stage toastStage;
    private Stage settigsStage;


    private Router() {
        this.theme = App.Properties.getTheme();
        setToastStage();
        setSettingsStage();

        try {
            setMainScene(theme.toString());
        } catch (IOException e) {
            ExceptionController.handleException(e);
        }
        try{
            setSettingsScene(theme.toString());
        } catch (IOException e) {
            ExceptionController.handleException(e);
        }

    }

    public static Router Singleton(){
        synchronized (Router.class){
            if(Instance == null){
                Instance = new Router();
            }
        }
        return Instance;
    }

    public void setMainStage(Stage stage){
        mainStage = stage;
        // Icon setzen
        Image icon = new Image(String.valueOf(Main.class.getResource("img/icons/counterClock.png")));
        mainStage.getIcons().add(icon);
        mainStage.setTitle("Memoria");
        mainStage.initStyle(StageStyle.DECORATED);
        mainStage.setScene(mainScene);
        App.DataStore.createSaveOnCloseMainWindowListener();
    }

    private void setToastStage(){
        toastStage = new Stage();
        toastStage.initStyle(StageStyle.TRANSPARENT);
        toastStage.initModality(Modality.NONE);//No Blocking other Windows
        toastStage.setAlwaysOnTop(true);
        toastStage.initOwner(this.mainStage); // Gehört zum Main-Window
    }

    public Stage getToastStage(){
        return toastStage;
    }

    private void setSettingsStage(){
        settigsStage = new Stage();
        settigsStage.initStyle(StageStyle.DECORATED);
        settigsStage.initModality(Modality.WINDOW_MODAL);//No Blocking other Windows
        settigsStage.setAlwaysOnTop(true);
    }

    public Stage getSettigsStage() {
        return settigsStage;
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public Scene getMainScene() {
        return mainScene;
    }

    public MainController getMainController() {
        return mainController;
    }

    public void setMainScene(String theme) throws IOException {

        SceneBuilder sceneBuilder = new SceneBuilder.Builder()
                .setFXML("Main.fxml")
                .setDimensions(App.Hardware.screenWidth()*0.8,App.Hardware.screenHeight()*0.8)
                .addStylesheet("css/main.css")
                .addStylesheet("css/basicWindow.css")
                .addStylesheet("css/themeElements.css")
                .addStylesheet("css/topToolBar.css")
                .addStylesheet("css/basicComponents.css")
                .addStylesheet("css/cards.css")
                .addStylesheet(theme)
                .build();
        mainScene = sceneBuilder.getScene();
        mainController = sceneBuilder.getController();
    }

    public SceneBuilder createMergeDetail(String theme) {
        return new SceneBuilder.Builder()
                .setFXML("mergeDetail.fxml")
                .setDimensions(App.Hardware.screenWidth()*0.5,App.Hardware.screenHeight()*0.7)
                .addStylesheet("css/basicWindow.css")
                .addStylesheet("css/mergeDetail.css")
                .addStylesheet("css/topToolBar.css")
                .addStylesheet("css/basicComponents.css")
                .addStylesheet("css/themeElements.css")
                .addStylesheet(theme)
                .build();
    }

    public SceneBuilder createMessage(String theme) {
        return new SceneBuilder.Builder()
                .setFXML("message.fxml")
                .setDimensions(App.Hardware.screenWidth()*0.4, App.Hardware.screenHeight()*0.4)
                .addStylesheet("css/basicWindow.css")
                .addStylesheet("css/themeElements.css")
                .addStylesheet("css/basicComponents.css")
                .addStylesheet("css/messages.css")
                .addStylesheet(theme)
                .build();
    }

    public SceneBuilder createToast(String theme) {
        return new SceneBuilder.Builder()
                .setFXML("toast.fxml")
                .setDimensions(App.Hardware.screenWidth()*0.2, App.Hardware.screenHeight()*0.065)
                .addStylesheet("css/basicWindow.css")
                .addStylesheet("css/themeElements.css")
                .addStylesheet("css/basicComponents.css")
                .addStylesheet("css/messages.css")
                .addStylesheet(theme)
                .build();
    }

    public Scene getSettingsScene() {
        return settingsScene;
    }

    public SettingsController getSettingsController() {
        return settingsController;
    }

    public void setSettingsScene(String theme) throws IOException {
        SceneBuilder sceneBuilder = new SceneBuilder.Builder()
                .setFXML("settings.fxml")
                .setDimensions(App.Hardware.screenWidth()*0.4,App.Hardware.screenHeight()*0.75)
                .addStylesheet("css/basicWindow.css")
                .addStylesheet("css/basicComponents.css")
                .addStylesheet("css/themeElements.css")
                .addStylesheet("css/settings.css")
                .addStylesheet(theme)
                .build();
        settingsScene = sceneBuilder.getScene();
        settingsController = sceneBuilder.getController();
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
        try {
            setMainScene(theme.toString());
            setSettingsScene(theme.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
