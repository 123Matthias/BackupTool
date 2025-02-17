package my.backup.backupTool;

import javafx.application.Application;
import javafx.stage.Stage;
import my.backup.backupTool.Model.BaseModel;


import java.io.IOException;

public class Main extends Application {

public final App App = new App();
    @Override
    public void start(Stage stage) throws IOException {

        App.Router.setMainStage(stage);
        App.Router.getMainStage().show();
        App.Router.getMainStage().centerOnScreen();

    }


    public static void main(String[] args) {
        launch();
    }
}