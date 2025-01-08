package my.backup.backuptool;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainApplication extends Application {

    public static Stage mainStage; // Instanzvariable für die Stage

    @Override
    public void start(Stage stage) throws IOException {
        setAndShowMainStage(stage);
    }

    private void setAndShowMainStage(Stage stage) throws IOException {
        MainApplication.mainStage = stage;
        FXMLLoader fxmlMain = new FXMLLoader(MainApplication.class.getResource("Main.fxml"));
        Scene scene = new Scene(fxmlMain.load(), 800, 800);
        mainStage.setTitle("Resurrection");
        mainStage.setMinWidth(800);
        mainStage.setMinHeight(800);
        scene.getStylesheets().add(getClass().getResource("css/main.css").toString());
        mainStage.setScene(scene);
        mainStage.show();
        }


    public static void main(String[] args) {
        launch();
    }
}