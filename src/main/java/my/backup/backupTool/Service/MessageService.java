package my.backup.backupTool.Service;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import my.backup.backupTool.App;
import my.backup.backupTool.Controller.MessageController;
import my.backup.backupTool.MessageTYPE;
import my.backup.backupTool.SceneBuilder;
import my.backup.backupTool.ToastTYPE;

import java.io.IOException;

public class MessageService {


    private static boolean isToastActive = false;

    public static void createMessage(IMessageList messages, MessageTYPE messageType) {

        Stage stage = new Stage();
        SceneBuilder sceneBuilder = App.Router.createMessage(App.Router.getTheme().toString());

        Scene scene = null;
        try {
            scene = sceneBuilder.getScene();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MessageController controller = sceneBuilder.getController();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(MessageTYPE.VALIDATION.toString());
        stage.setScene(scene);
        stage.show();
        controller.showMessage(messages, messageType);
    }



    public static void createToast(String toastText) {

        if (isToastActive) {
            return;
        }

        isToastActive = true;

        Stage stage = App.Router.getToastStage();
        SceneBuilder sceneBuilder = App.Router.createToast(App.Router.getTheme().toString());

        Scene scene = null;
        try {
            scene = sceneBuilder.getScene();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MessageController controller = sceneBuilder.getController();

        stage.setScene(scene);
        controller.showToast(toastText);

        scene.setFill(Color.TRANSPARENT);

        // Positioniere die Toast-Stage mittig zur Main-Stage
        centerStageOnAnother(stage, App.Router.getMainStage());

        // Animationen
        FadeTransition fadeOut = new FadeTransition(Duration.millis(2000), stage.getScene().getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        PauseTransition pause = new PauseTransition(Duration.millis(2500));
        pause.setOnFinished(event -> {
            fadeOut.play();
        });

        fadeOut.setOnFinished(event -> {
            stage.close();
            isToastActive = false;
        });

        stage.show();
        pause.play();
    }

    private static void centerStageOnAnother(Stage stageToCenter, Stage referenceStage) {

        // Verhindern, dass die Stage sofort sichtbar wird (unsichtbar machen)
        stageToCenter.setOpacity(0);
        // Zeige die Stage vorübergehend an, um ihre Größe zu ermitteln
        stageToCenter.show();

        double referenceWidth = referenceStage.getWidth();
        double referenceHeight = referenceStage.getHeight();
        double referenceX = referenceStage.getX();
        double referenceY = referenceStage.getY();

        double stageWidth = stageToCenter.getWidth();
        double stageHeight = stageToCenter.getHeight();

        double stageX = referenceX + (referenceWidth - stageWidth) / 2;
        double stageY = referenceY + (referenceHeight - stageHeight) / 2;

        stageToCenter.setX(stageX);
        stageToCenter.setY(stageY);

        // Jetzt die Stage sichtbar machen
        stageToCenter.setOpacity(1);
    }
}
