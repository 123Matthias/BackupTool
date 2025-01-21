package my.backup.backupTool;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import my.backup.backupTool.Controller.ExceptionController;

import java.io.IOException;

public class Router {

    private Stage mainStage;
    private Scene sceneMain;
    private Scene sceneMergeOverview;
    private Scene sceneSettings;
    private Theme theme;

    public Router() {
        this.theme = Theme.DARK;
        try {
            setBaseOverviewScene(theme.toString());
        } catch (IOException e) {
            ExceptionController.handleException(e);
        }

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
    }

    public void setMainStage(Stage stage){
        mainStage = stage;

        // Icon setzen
        Image icon = new Image(String.valueOf(Main.class.getResource("img/FeenFluegel.png")));
        mainStage.getIcons().add(icon);
        mainStage.setTitle("ccBackup");
        mainStage.initStyle(StageStyle.DECORATED); // Kein Hintergrund für die Scene
        mainStage.setScene(sceneMain);
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

    public SceneBuilder createNew_MergeDetailView_With_SceneBuilderObject(String theme) {
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

    public void setBaseOverviewScene(String theme) throws IOException {
        SceneBuilder sceneBuilder = new SceneBuilder.Builder()
                .setFXML("mergeOverview.fxml")
                .setDimensions(800, 800)
                .addStylesheet("css/basicWindow.css")
                .addStylesheet("css/mergeOverview.css")
                .addStylesheet("css/basicComponents.css")
                .addStylesheet("css/cards.css")
                .addStylesheet("css/toolBar.css")
                .addStylesheet("css/themeElements.css")
                .addStylesheet(theme)
                .build();
        sceneMergeOverview = sceneBuilder.getScene();
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
            setBaseOverviewScene(theme.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
