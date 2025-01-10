package my.backup.backupTool;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static Stage mainStage;
    public static Scene sceneMain;
    public static Scene sceneMerge;

    @Override
    public void start(Stage stage) throws IOException {
        setAndShowMainStage(stage);
    }

    private void setAndShowMainStage(Stage stage) throws IOException {
        Main.mainStage = stage;
        FXMLLoader fxmlMain = new FXMLLoader(Main.class.getResource("Main.fxml"));
        FXMLLoader fxmlMerge = new FXMLLoader(Main.class.getResource("merge.fxml"));
        sceneMain = new Scene(fxmlMain.load(), 800, 800);
        sceneMerge = new Scene(fxmlMerge.load(), 800, 800);
        mainStage.setTitle("Resurrection");
        mainStage.setMinWidth(800);
        mainStage.setMinHeight(800);
        sceneMain.getStylesheets().add(String.valueOf(getClass().getResource("css/main.css")));
        sceneMerge.getStylesheets().add(String.valueOf(getClass().getResource("css/merge.css")));
        mainStage.setScene(sceneMain);
        mainStage.show();
        }


    public static void main(String[] args) {
        launch();
    }
}