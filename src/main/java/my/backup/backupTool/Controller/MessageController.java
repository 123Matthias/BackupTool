package my.backup.backupTool.Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import my.backup.backupTool.App;
import my.backup.backupTool.SceneBuilder;
import my.backup.backupTool.Service.IMessageList;
import java.io.IOException;

public class MessageController implements IMessageController {

    @Override
    public void show(IMessageList messages) {
        Stage stage = new Stage();
        SceneBuilder sceneBuilder = App.Router.createMessage(App.Router.getTheme().toString());
        Scene scene = null;

        try {
            scene = sceneBuilder.getScene();
        } catch (IOException e) {
            ExceptionController.handleException(e);
            return; // Abbrechen, falls ein Fehler auftritt
        }

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Validation");
        stage.setScene(scene);

        // Hole das Root-Element (AnchorPane) aus der geladenen Szene
        AnchorPane rootPane = (AnchorPane) scene.getRoot();
        rootPane.getStyleClass().add("basicBackground");

        // Erstelle eine VBox für die Nachrichten
        VBox messageBox = new VBox();
        messageBox.setSpacing(10);

        // Füge die Nachrichten als Labels hinzu
        for (String message : messages.getMessagesAsList()) {
            Label messageLabel = new Label(message);
            messageLabel.setStyle("-fx-font-size: 16px; -fx-wrap-text: true; -fx-padding: 5px;");
            messageBox.getChildren().add(messageLabel);
        }

        // Positioniere die VBox innerhalb des AnchorPane
        AnchorPane.setTopAnchor(messageBox, 150.0);
        AnchorPane.setLeftAnchor(messageBox, 50.0);
        AnchorPane.setRightAnchor(messageBox, 50.0);
        rootPane.getChildren().add(messageBox);

        // Zeige die neue Stage an
        stage.showAndWait();
    }



}
