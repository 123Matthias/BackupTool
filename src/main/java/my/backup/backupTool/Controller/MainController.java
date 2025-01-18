package my.backup.backupTool.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import my.backup.backupTool.App;
import my.backup.backupTool.Main;

import java.io.IOException;

//import static my.backup.backupTool.Main.sceneMerge;

public class MainController {

/*
        @FXML
        private void handleOpenMergeWindow() throws IOException {
            Main.mainStage.setTitle("Merge");
            Main.mainStage.setMinWidth(800);
            Main.mainStage.setMinHeight(800);
            Main.mainStage.setScene(sceneMerge);
            Main.mainStage.show();
        }
*/
    @FXML
    public void handleOpenMergeOverviewWindow() {
        Main.mainStage.setMinWidth(800);
        Main.mainStage.setMinHeight(800);
        Main.mainStage.setScene(App.sceneMergeOverview);
        Main.mainStage.show();
    }



    public void handleBackToMainButton() throws IOException {
        Main.mainStage.show();

    }

    }

