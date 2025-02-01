package my.backup.backupTool;

import javafx.application.Application;
import javafx.stage.Stage;
import my.backup.backupTool.Model.BaseModel;


import java.io.IOException;

public class Main extends Application {

public static App App;
    @Override
    public void start(Stage stage) throws IOException {

        App = new App();
        App.Router.setMainStage(stage);  // Sicherstellen, dass app.Router hier nicht null ist
        App.Router.getMainStage().show();

    }


    public static void main(String[] args) {
        launch();
    }
}