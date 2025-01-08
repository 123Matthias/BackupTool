package my.backup.backuptool.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import my.backup.backuptool.MainApplication;

import java.io.IOException;

public class MainController {


        @FXML
        private void handleOpenMergeWindow() throws IOException {
            FXMLLoader fxmlMergeBackup = new FXMLLoader(MainApplication.class.getResource("merge.fxml"));
            Scene scene = new Scene(fxmlMergeBackup.load(), 800, 800);
            MainApplication.mainStage.setTitle("Merge");
            MainApplication.mainStage.setMinWidth(800);
            MainApplication.mainStage.setMinHeight(800);
            scene.getStylesheets().add(String.valueOf(getClass().getResource("css/main.css")));
            MainApplication.mainStage.setScene(scene);
            MainApplication.mainStage.show();
        }

    public void handleBackToMainButton() throws IOException {
        MainApplication.mainStage.show();

    }

    }

