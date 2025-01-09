package my.backup.backuptool;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;

public class MainApplication extends Application {

    public static Stage mainStage;
    public static Scene sceneMain;
    public static Scene sceneMerge;

    @Override
    public void start(Stage stage) throws IOException {
        setAndShowMainStage(stage);
    }

    private void setAndShowMainStage(Stage stage) throws IOException {
        MainApplication.mainStage = stage;
        FXMLLoader fxmlMain = new FXMLLoader(MainApplication.class.getResource("Main.fxml"));
        FXMLLoader fxmlMerge = new FXMLLoader(MainApplication.class.getResource("merge.fxml"));
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