package my.backup.backuptool.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import my.backup.backuptool.MainApplication;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;

import static my.backup.backuptool.MainApplication.sceneMerge;

public class MainController {


        @FXML
        private void handleOpenMergeWindow() throws IOException {
            MainApplication.mainStage.setTitle("Merge");
            MainApplication.mainStage.setMinWidth(800);
            MainApplication.mainStage.setMinHeight(800);
            MainApplication.mainStage.setScene(sceneMerge);
            MainApplication.mainStage.show();
        }

    public void handleBackToMainButton() throws IOException {
        MainApplication.mainStage.show();

    }

    }

