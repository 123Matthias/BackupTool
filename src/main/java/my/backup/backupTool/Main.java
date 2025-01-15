package my.backup.backupTool;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;

public class Main extends Application {

    public static Stage mainStage;
    public static Scene sceneMain;
    public static Scene sceneMerge;
    public static Scene sceneMergeOverview;

    @Override
    public void start(Stage stage) throws IOException {
        setAndShowMainStage(stage);
        setMergeOverviewWindow();
        setMergeDetailViewWindow();
        mainStage.show();
    }

    private void setAndShowMainStage(Stage stage) throws IOException {
        Main.mainStage = stage;
        FXMLLoader fxmlMain = new FXMLLoader(Main.class.getResource("Main.fxml"));
        sceneMain = new Scene(fxmlMain.load(), 800, 800);
        mainStage.setTitle("ccBackup");
        mainStage.setMinWidth(800);
        mainStage.setMinHeight(800);
        // Icon setzen
        Image icon = new Image(String.valueOf(Main.class.getResource("img/frame-rund.png")));
        mainStage.getIcons().add(icon);
        sceneMain.getStylesheets().add(String.valueOf(getClass().getResource("css/main.css")));
        sceneMain.getStylesheets().add(String.valueOf(getClass().getResource("css/basicWindow.css")));
        sceneMain.setFill(javafx.scene.paint.Color.TRANSPARENT); // Kein Hintergrund für die Scene
        mainStage.initStyle(StageStyle.DECORATED); // Kein Hintergrund für die Scene
        mainStage.setScene(sceneMain);

        }

        private void setMergeOverviewWindow() throws IOException {
            FXMLLoader fxmlMergeOverview = new FXMLLoader(Main.class.getResource("mergeOverview.fxml"));
            sceneMergeOverview = new Scene(fxmlMergeOverview.load(), 800, 800);
            sceneMergeOverview.getStylesheets().add(String.valueOf(getClass().getResource("css/cards.css")));
            sceneMergeOverview.getStylesheets().add(String.valueOf(getClass().getResource("css/mergeOverview.css")));
            sceneMergeOverview.getStylesheets().add(String.valueOf(getClass().getResource("css/buttons.css")));
        }

        private void setMergeDetailViewWindow() throws IOException {
            FXMLLoader fxmlMerge = new FXMLLoader(Main.class.getResource("merge.fxml"));
            sceneMerge = new Scene(fxmlMerge.load(), 800, 800);
            sceneMerge.getStylesheets().add(String.valueOf(getClass().getResource("css/merge.css")));
        }



    public static void main(String[] args) {
        launch();
    }
}