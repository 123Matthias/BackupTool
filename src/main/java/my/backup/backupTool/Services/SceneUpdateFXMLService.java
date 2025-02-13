package my.backup.backupTool.Services;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import my.backup.backupTool.App;
import my.backup.backupTool.Main;
import my.backup.backupTool.Notifications.IUpdateScene;

import java.io.IOException;

public class SceneUpdateFXMLService implements IUpdateScene {

    public void reloadView(String fxmlTemplate) {
        // FXML-Datei neu laden
        FXMLLoader fxmlMergeOverview = new FXMLLoader(Main.class.getResource(fxmlTemplate));
        // Die Root-Komponente der neuen Szene laden
        Parent newRoot = null;
        try {
            newRoot = fxmlMergeOverview.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Hole die aktuelle Scene und aktualisiere nur den Inhalt
        Scene currentScene = App.Router.getMainStage().getScene();
        currentScene.setRoot(newRoot);

        // Zeige die aktualisierte Szene ohne den Scene-Objekt zu wechseln
        App.Router.getMainStage().show();
    }
}
