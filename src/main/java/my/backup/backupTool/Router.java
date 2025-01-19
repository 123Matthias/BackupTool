package my.backup.backupTool;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import my.backup.backupTool.Controller.ExceptionController;
import my.backup.backupTool.Controller.SceneMergeDetailController;

import java.io.IOException;

public class Router {

    private Stage mainStage;
    private Scene sceneMain;
    private Scene sceneMergeDetail;
    private Scene sceneMergeOverview;
    private SceneMergeDetailController mergeDetailController;


    public Router() {

        try {
            setBaseOverviewScene();
        } catch (IOException e) {
            ExceptionController.handleException(e);
        }

        try {
            setMainScene();
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

    public void setMainScene() throws IOException {
        SceneBuilder sceneBuilder = new SceneBuilder.Builder()
                .setFXML("Main.fxml")
                .setDimensions(800,800)
                .addStylesheet("css/main.css")
                .addStylesheet("css/basicWindow.css")
                .build();
        sceneMain = sceneBuilder.getScene();

    }

    public SceneBuilder createNew_MergeDetailView_With_SceneBuilderObject() {
        return new SceneBuilder.Builder()
                .setFXML("mergeDetail.fxml")
                .setDimensions(800, 800)
                .addStylesheet("css/basicWindow.css")
                .addStylesheet("css/mergeDetail.css")
                .addStylesheet("css/buttons.css")
                .build();
    }

    public void setBaseOverviewScene() throws IOException {
        SceneBuilder sceneBuilder = new SceneBuilder.Builder()
                .setFXML("mergeOverview.fxml")
                .setDimensions(800, 800)
                .addStylesheet("css/basicWindow.css")
                .addStylesheet("css/mergeOverview.css")
                .addStylesheet("css/basicComponents.css")
                .addStylesheet("css/cards.css")
                .addStylesheet("css/buttons.css")
                .build();
        sceneMergeOverview = sceneBuilder.getScene();
    }

    public Scene getSceneMergeDetail() {
        return sceneMergeDetail;
    }

    public Scene getSceneMergeOverview() {
        return sceneMergeOverview;
    }

    public SceneMergeDetailController getMergeDetailController() {
        return mergeDetailController;
    }
}
