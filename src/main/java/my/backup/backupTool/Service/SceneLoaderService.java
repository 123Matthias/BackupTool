package my.backup.backupTool.Service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import my.backup.backupTool.Main;

import java.io.IOException;

public class SceneLoaderService {

    public static void reloadView(String fxmlTemplate) throws IOException {
        // FXML-Datei neu laden
        FXMLLoader fxmlMergeOverview = new FXMLLoader(Main.class.getResource(fxmlTemplate));
        // Die Root-Komponente der neuen Szene laden
        Parent newRoot = fxmlMergeOverview.load();

        // Hole die aktuelle Scene und aktualisiere nur den Inhalt
        Scene currentScene = Main.mainStage.getScene();
        currentScene.setRoot(newRoot);

        // Zeige die aktualisierte Szene ohne den Scene-Objekt zu wechseln
        Main.mainStage.show();
    }
}
