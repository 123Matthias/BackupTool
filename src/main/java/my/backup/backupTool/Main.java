package my.backup.backupTool;

import javafx.application.Application;
import javafx.stage.Stage;
import my.backup.backupTool.Model.IModel;


import java.io.IOException;

public class Main extends Application {

public static App App;
    @Override
    public void start(Stage stage) throws IOException {

        App = new App();
        App.Router.setMainStage(stage);  // Sicherstellen, dass app.Router hier nicht null ist
        App.Router.getMainStage().show();

        for(IModel m : App.JobScheduler.getBackupOrderList()){
            System.out.println("Job Scheduler UID: " + m.getUid() +
                    "\n Title: " + m.getTitle() +
                    "\n Backup Order: " + m.hasPlayBackupOrder() +
                    "\n Backup Type: " + m.getBackupType());
        }

    }


    public static void main(String[] args) {
        launch();
    }
}