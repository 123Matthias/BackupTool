package my.backup.backupTool;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SceneBuilder {
    private String fxmlFile;
    private double width;
    private double height;
    private final List<String> stylesheets = new ArrayList<>();
    private FXMLLoader fxmlLoader;

    // Innerer Builder
    public static class Builder {
        private final SceneBuilder instance = new SceneBuilder();

        public Builder setFXML(String fxmlFile) {
            instance.fxmlFile = fxmlFile;
            return this;
        }

        public Builder setDimensions(double width, double height) {
            instance.width = width;
            instance.height = height;
            return this;
        }

        public Builder addStylesheet(String stylesheet) {
            instance.stylesheets.add(stylesheet);
            return this;
        }

        public SceneBuilder build() {
            instance.fxmlLoader = new FXMLLoader(SceneBuilder.class.getResource(instance.fxmlFile));
            return instance;
        }
    }

    // Methode, um den Scene-Root (Parent) zu bekommen
    public Parent getRoot() throws IOException {
        if (fxmlLoader == null) {
            throw new IllegalStateException("FXMLLoader not initialized. Use the Builder to configure the SceneBuilder.");
        }
        // Lade das FXML und extrahiere den root
        return fxmlLoader.load();
    }

    // Methode, um die Scene zu erstellen
    public Scene getScene() throws IOException {
        Parent root = getRoot();  // Hole den root von getRoot()
        Scene scene = new Scene(root, width, height);  // Erstelle Scene mit root
        for (String stylesheet : stylesheets) {
            scene.getStylesheets().add(String.valueOf(getClass().getResource(stylesheet)));
        }
        return scene;
    }

    // Methode, um den Controller zu bekommen
    public <T> T getController() {
        if (fxmlLoader == null) {
            throw new IllegalStateException("FXMLLoader not initialized. Use the Builder to configure the SceneBuilder.");
        }
        return fxmlLoader.getController();
    }
}
