package my.backup.backupTool.Controller;

import javafx.fxml.FXML;
import my.backup.backupTool.App;
import my.backup.backupTool.Main;

import java.io.IOException;

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
        App.Router.getMainStage().setScene(App.Router.getSceneMergeOverview());
        App.Router.getMainStage().show();
    }



    public void handleBackToMainButton() throws IOException {


    }

    }

