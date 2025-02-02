package my.backup.backupTool;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import my.backup.backupTool.Controller.ExceptionController;

import java.io.IOException;

public class Router {

    private Stage mainStage;
    private Scene sceneMain;
    private SceneBuilder sceneBuilderMergeOverview;
    private Scene sceneMergeOverview;
    private Scene sceneSettings;
    private Theme theme;
    private Stage toastStage;

    public Router() {
        this.theme = Theme.DARK;
        setToastStage();

        try {
            setMainScene(theme.toString());
        } catch (IOException e) {
            ExceptionController.handleException(e);
        }
        try{
            setSceneSettings(theme.toString());
        } catch (IOException e) {
            ExceptionController.handleException(e);
        }

        try {
            setSceneMergeOverview(theme.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setMainStage(Stage stage){
        mainStage = stage;

        // Icon setzen
        Image icon = new Image(String.valueOf(Main.class.getResource("img/FeenFluegel.png")));
        mainStage.getIcons().add(icon);
        mainStage.setTitle("Memoria");
        mainStage.initStyle(StageStyle.DECORATED); // Kein Hintergrund für die Scene
        mainStage.setScene(sceneMain);
    }

    private void setToastStage(){
        toastStage = new Stage();
        toastStage.initStyle(StageStyle.TRANSPARENT);
        toastStage.initModality(Modality.NONE);
        toastStage.setAlwaysOnTop(true);
    }

    public Stage getToastStage(){
        return toastStage;
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public Scene getSceneMain() {
        return sceneMain;
    }

    public void setMainScene(String theme) throws IOException {
        SceneBuilder sceneBuilder = new SceneBuilder.Builder()
                .setFXML("Main.fxml")
                .setDimensions(800,800)
                .addStylesheet("css/main.css")
                .addStylesheet("css/basicWindow.css")
                .addStylesheet("css/themeElements.css")
                .addStylesheet(theme)
                .build();
        sceneMain = sceneBuilder.getScene();
    }

    public SceneBuilder createMergeDetail(String theme) {
        return new SceneBuilder.Builder()
                .setFXML("mergeDetail.fxml")
                .setDimensions(800, 800)
                .addStylesheet("css/basicWindow.css")
                .addStylesheet("css/mergeDetail.css")
                .addStylesheet("css/toolBar.css")
                .addStylesheet("css/basicComponents.css")
                .addStylesheet("css/themeElements.css")
                .addStylesheet(theme)
                .build();
    }

    public SceneBuilder createMessage(String theme) {
        return new SceneBuilder.Builder()
                .setFXML("message.fxml")
                .setDimensions(400, 300)
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
                .setDimensions(350, 80)
                .addStylesheet("css/basicWindow.css")
                .addStylesheet("css/themeElements.css")
                .addStylesheet("css/basicComponents.css")
                .addStylesheet("css/messages.css")
                .addStylesheet(theme)
                .build();
    }

    public void setSceneMergeOverview(String theme) throws IOException {
        SceneBuilder sceneBuilder = new SceneBuilder.Builder()
                .setFXML("baseOverview.fxml")
                .setDimensions(800, 800)
                .addStylesheet("css/basicWindow.css")
                .addStylesheet("css/baseOverview.css")
                .addStylesheet("css/basicComponents.css")
                .addStylesheet("css/cards.css")
                .addStylesheet("css/toolBar.css")
                .addStylesheet("css/themeElements.css")
                .addStylesheet(theme)
                .build();
        sceneMergeOverview = sceneBuilder.getScene();
    }

    public SceneBuilder getSceneBuilderMergeOverview() {
        return sceneBuilderMergeOverview;
    }

    public Scene getSceneMergeOverview() {
        return sceneMergeOverview;
    }

    public Scene getSceneSettings() {
        return sceneSettings;
    }

    public void setSceneSettings(String theme) throws IOException {
        SceneBuilder sceneBuilder = new SceneBuilder.Builder()
                .setFXML("settings.fxml")
                .setDimensions(600,600)
                .addStylesheet("css/basicWindow.css")
                .addStylesheet("css/toolBar.css")
                .addStylesheet("css/themeElements.css")
                .addStylesheet("css/settings.css")
                .addStylesheet(theme)
                .build();
        sceneSettings = sceneBuilder.getScene();
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
        try {
            setMainScene(theme.toString());
            setSceneSettings(theme.toString());
            setSceneMergeOverview(theme.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
