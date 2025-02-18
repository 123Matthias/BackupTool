package my.backup.backupTool.Controller;

import javafx.fxml.FXML;
import my.backup.backupTool.App;
import my.backup.backupTool.Theme;

public class SettingsController {

    @FXML
    public void handleLightTheme(){
        App.Router.setTheme(Theme.LIGHT);
        App.Router.getMainStage().setScene(App.Router.getMainScene());
        App.Router.getSettigsStage().setScene(App.Router.getSceneSettings());
        App.Router.getMainStage().show();
    }

    @FXML
    public void handleDarkTheme(){
        App.Router.setTheme(Theme.DARK);
        App.Router.getMainStage().setScene(App.Router.getMainScene());
        App.Router.getSettigsStage().setScene(App.Router.getSceneSettings());
        App.Router.getMainStage().show();
    }

    @FXML
    public void backToMain(){
        //  Main.mainStage.setScene(Main.sceneMain);
        App.Router.getMainStage().setScene(App.Router.getMainScene());
        App.Router.getMainStage().show();
    }
    /*
    @FXML
    public void setLightTheme() {

        App.Router.clearAllStylesheets();

        // Setze den Hintergrund für das helle Thema
      root.setStyle("-fx-background-color: linear-gradient(to bottom right, "
                + "rgba(230,230,235, 1) 20%, "
                + "rgba(240,240,245, 1) 60%, "
                + "rgba(250,250,255, 1) 80%, "
                + "rgba(255,255,255, 1) 100%);");
    }

    @FXML
    public void setDarkTheme() {
        // Setze den Hintergrund für das dunkle Thema
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, "
                + "rgba(30,30,35, 1) 20%, "
                + "rgba(40,40,45, 1) 60%, "
                + "rgba(50,50,55, 1) 80%, "
                + "rgba(60,60,65, 1) 100%);");
    }
*/
}
