package my.backup.backupTool;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import my.backup.backupTool.Controller.ExceptionController;
import my.backup.backupTool.Controller.SceneMergeDetailController;
import my.backup.backupTool.Service.SceneUpdateFXMLService;

import java.io.FileReader;
import java.io.IOException;

public class App {

    public static Stage mainStage;
    public static Scene sceneMain;
    public static Scene sceneMergeDetail;
    public static SceneMergeDetailController sceneMergeDetailController;

    public static Scene sceneMergeOverview;
    SceneBuilder sceneBuilder;

    public App(){
        try {
            setMergeDetailStage();
        } catch (IOException e) {
            ExceptionController.handleException(e);
        }

        try {
            setBaseOverviewStage();
        } catch (IOException e) {
            ExceptionController.handleException(e);
        }
    }

    public void setMergeDetailStage() throws IOException {
        sceneBuilder = new SceneBuilder.Builder()
                .setFXML("mergeDetail.fxml")
                .setDimensions(800, 800)
                .addStylesheet("css/basicWindow.css")
                .addStylesheet("css/mergeDetail.css")
                .addStylesheet("css/buttons.css")
                .build();
        sceneMergeDetail = sceneBuilder.getScene();
        sceneMergeDetailController = sceneBuilder.getController();
    }

    public void setBaseOverviewStage() throws IOException {
        sceneBuilder = new SceneBuilder.Builder()
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
}
